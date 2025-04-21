const connection = require('../../config/database')
require('dotenv').config()

const patient_info = async (req, res) => {
    const authorization_id = req.user.authorization_id; // Lấy từ token đã decode

    if (!authorization_id) {
        return res.status(400).json({ message: "authorization_id không tồn tại trong token" });
    }

    const sql = `
        SELECT p.full_name, p.patient_id, p.avatar
        FROM Patients p 
        JOIN Authorizations a ON p.authorization_id = a.authorization_id
        WHERE p.authorization_id = ?
    `;

    connection.query(sql, [authorization_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bệnh nhân" });
        }

        res.json({
            message: 'Success',
            data: {
                full_name: result[0].full_name, // Trả về tên riêng
                patient_id: result[0].patient_id,
                avatar: result[0].avatar
            }
        });
    });
};


const specialists = async (req, res) => {
    const sql = `SELECT specialist_id, specialist_name, icon FROM Specialists LIMIT 3`

    connection.query(sql, (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy chuyên khoa" });
        }
        res.json({
            message: 'Success',
            data: result
        });
    })
}

const top_doctors = async (req, res) => {
    const sql = `SELECT d.doctor_id, d.full_name, d.avatar, d.specialty, d.qualification,
                        ROUND(AVG(c.star), 1) AS average_star
                 FROM Doctors d
                 JOIN Comments c ON d.doctor_id = c.doctor_id
                 GROUP BY d.doctor_id, d.full_name, d.avatar
                 ORDER BY average_star DESC
                 LIMIT 5;`;

    connection.query(sql, (err, results) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (results.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bác sĩ" });
        }

        // Trả về avatar dưới dạng link trực tiếp
        const doctors = results.map((doctor) => ({
            doctor_id: doctor.doctor_id,
            full_name: doctor.full_name,
            specialty: doctor.specialty,
            qualification: doctor.qualification,
            average_star: doctor.average_star,
            avatar: doctor.avatar // Giữ nguyên link Cloudinary
        }));

        res.json({
            message: 'Success',
            data: doctors
        });
    });
};


const search_result = async (req, res) => {
    const sqlDoctors = `SELECT full_name FROM Doctors`;
    const sqlSpecialists = `SELECT DISTINCT specialist_name FROM specialists`;

    connection.query(sqlDoctors, (errDoctors, doctors) => {
        if (errDoctors) {
            return res.status(500).json({ error: "Lỗi truy vấn database (Doctors)" });
        }

        connection.query(sqlSpecialists, (errSpecialists, specialists) => {
            if (errSpecialists) {
                return res.status(500).json({ error: "Lỗi truy vấn database (Specialists)" });
            }

            // Format danh sách bác sĩ (Thêm "Bs.")
            const doctorList = doctors.map(doc => ({ name: `Bs. ${doc.full_name}` }));

            // Format danh sách chuyên khoa (Thêm "Ck.")
            const specialistList = specialists.map(spec => ({ name: `Ck. ${spec.specialist_name}` }));

            const responseData = [...doctorList, ...specialistList];

            res.json({
                message: "Success",
                data: responseData
            });
        });
    });
};


module.exports = {
    patient_info,
    specialists,
    top_doctors,
    search_result
}