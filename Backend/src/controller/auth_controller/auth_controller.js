const connection = require('../../config/database')
require('dotenv').config()
const jwt = require("jsonwebtoken")
const bcrypt = require('bcrypt')
const crypto = require('crypto');
const transporter = require('../../config/nodemailer')

const SECRET_KEY = process.env.SECRET_KEY

const login = async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: "Email hoặc password không được để trống" });
    }

    // Lấy thông tin người dùng dựa trên email
    const query = `SELECT authorization_id, email, password_hash, role FROM Authorizations WHERE email = ?`;

    connection.query(query, [email], async (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ" });
        }
        if (results.length > 0) {
            const user = results[0];

            // So sánh mật khẩu người dùng nhập với mật khẩu đã mã hóa
            const isPasswordValid = await bcrypt.compare(password, user.password_hash);
            if (!isPasswordValid) {
                return res.status(401).json({ message: "Email hoặc password không chính xác" });
            }

            // Tạo token JWT
            const token = jwt.sign(
                { authorization_id: user.authorization_id, role: user.role },
                SECRET_KEY,
                { expiresIn: '24h' }
            );

            return res.json({
                message: 'Success',
                token,
                authorization_id: user.authorization_id,
                role: user.role
            });
        } else {
            return res.status(401).json({ message: "Email hoặc password không chính xác" });
        }
    });
};


const verificationCodes = {};

const forget_password = async (req, res) => {
    const { email } = req.body;

    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        return res.status(400).json({ message: 'Email không hợp lệ' });
    }

    try {
        // Kiểm tra email trong DB
        const [rows] = await connection.promise().query('SELECT email FROM Authorizations WHERE email = ?', [email]);

        if (rows.length === 0) {
            return res.status(404).json({ message: 'Email không tồn tại trong hệ thống' });
        }

        console.log('Email hợp lệ, tiến hành gửi mã:', email);

        // Tạo mã xác nhận
        const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
        verificationCodes[email] = verificationCode;

        // Gửi email
        await transporter.sendMail({
            from: process.env.EMAIL_USER,
            to: email,
            subject: 'Mã xác nhận của bạn',
            text: `Mã xác nhận của bạn là: ${verificationCode}`
        });

        res.json({ message: 'Success send code', type: "ForgetPassword" });

    } catch (error) {
        console.log('Lỗi trong quá trình xử lý:', error);
        res.status(500).json({ message: 'Lỗi server', error: error.message });
    }
};

const verify_code = async (req, res) => {

    const { email, code, type } = req.body;

    if (!email || !code) {
        return res.status(400).json({ message: 'Vui lòng nhập email và mã xác nhận' });
    }

    if (verificationCodes[email] && verificationCodes[email] === code) {
        delete verificationCodes[email]; // Xoá mã sau khi xác thực thành công
        return res.json({ message: 'Success', type });
    } else {
        return res.status(400).json({ message: 'Mã xác nhận không hợp lệ' });
    }
};


const reset_password = async (req, res) => {
    const { email, newPassword } = req.body;

    if (!email || !newPassword) {
        return res.status(400).json({ message: "Vui lòng nhập email và mật khẩu mới" });
    }

    const saltRounds = 10;
    bcrypt.hash(newPassword, saltRounds, (hashErr, hashedPassword) => {
        if (hashErr) {
            console.error("Lỗi băm mật khẩu:", hashErr.message);
            return res.status(500).json({ message: "Lỗi khi đặt lại mật khẩu" });
        }

        const query = `UPDATE authorizations SET password_hash = ? WHERE email = ?`;

        connection.query(query, [hashedPassword, email], (err, result) => {
            if (err) {
                console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
                return res.status(500).json({ message: "Lỗi máy chủ" });
            }

            if (result.affectedRows === 0) {
                return res.status(404).json({ message: "Email không tồn tại trong hệ thống" });
            }

            res.json({ message: "Reset Password Success" });
        });
    });
};

const sign_up = async (req, res) => {
    const { full_name, email, password } = req.body;

    // Kiểm tra định dạng email
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        return res.status(400).json({ message: "Email không hợp lệ" });
    }

    try {
        // Kiểm tra email có tồn tại trong DB không
        const [rows] = await connection.promise().query('SELECT email FROM Authorizations WHERE email = ?', [email]);

        if (rows.length > 0) {
            return res.status(409).json({ message: "Email đã tồn tại" }); // 409: Conflict
        }

        console.log("Email hợp lệ, tiến hành gửi mã:", email);

        // Tạo mã xác nhận
        const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
        verificationCodes[email] = verificationCode;

        // Gửi email
        await transporter.sendMail({
            from: process.env.EMAIL_USER,
            to: email,
            subject: "Mã xác nhận đăng ký",
            text: `Mã xác nhận của bạn là: ${verificationCode}`
        });

        res.json({ message: "Success send code", type: "SignUp" });

    } catch (error) {
        console.log("Lỗi trong quá trình xử lý:", error);
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};


const creat_profile = async (req, res) => {
    const { email, password, full_name, phone_number, date_of_birth, gender, address, avatar } = req.body;

    if (!email || !password || !full_name || !phone_number || !date_of_birth || !gender || !address) {
        return res.status(400).json({ message: "Vui lòng nhập đầy đủ thông tin" });
    }

    try {
        // Kiểm tra xem email đã tồn tại chưa
        const [existingUser] = await connection.promise().query('SELECT authorization_id FROM Authorizations WHERE email = ?', [email]);

        if (existingUser.length > 0) {
            return res.status(409).json({ message: "Email đã tồn tại" }); // 409: Conflict
        }

        // Mã hóa mật khẩu
        const hashedPassword = await bcrypt.hash(password, 10);

        // Bắt đầu transaction để đảm bảo tính toàn vẹn dữ liệu
        const conn = await connection.promise().getConnection();
        await conn.beginTransaction();

        try {
            // Thêm user vào bảng Authorizations
            const [authResult] = await conn.query(
                'INSERT INTO Authorizations (email, password_hash, role) VALUES (?, ?, ?)',
                [email, hashedPassword, "Patient"]
            );

            const authorization_id = authResult.insertId; // Lấy ID vừa thêm vào

            // Thêm profile vào bảng Patients
            await conn.query(
                'INSERT INTO Patients (authorization_id, full_name, phone_number, date_of_birth, gender, address, avatar) VALUES (?, ?, ?, ?, ?, ?, ?)',
                [authorization_id, full_name, phone_number, date_of_birth, gender, address, avatar || null]
            );

            // Commit transaction nếu không có lỗi
            await conn.commit();
            conn.release();

            res.status(201).json({ message: "Success", authorization_id });

        } catch (error) {
            // Rollback nếu có lỗi
            await conn.rollback();
            conn.release();
            throw error;
        }

    } catch (error) {
        console.log("Lỗi khi tạo hồ sơ:", error);
        res.status(500).json({ message: "Lỗi server", error: error.message });
    }
};



module.exports = {
    login,
    forget_password,
    verify_code,
    reset_password,
    sign_up,
    creat_profile
}