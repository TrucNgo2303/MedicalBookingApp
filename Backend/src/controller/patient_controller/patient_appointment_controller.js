const connection = require('../../config/database')
const moment = require('moment-timezone');
require('dotenv').config()


const doctor_detail = async (req, res) => {
    const { doctor_id } = req.body; // Lấy doctor_id từ body request

    if (!doctor_id) {
        return res.status(400).json({ message: "Doctor_id trống" });
    }

    const query = `
        SELECT 
            d.doctor_id, 
            d.full_name, 
            d.phone_number, 
            d.specialty, 
            d.qualification, 
            d.experience_years, 
            d.summary, 
            d.avatar, 
            d.patient_count, 
            s.consultation_fee,
            ROUND(COALESCE(AVG(c.star), 0), 1) AS average_star, 
            COUNT(c.comment_id) AS total_comments
        FROM Doctors d
        LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
        LEFT JOIN Specialists s ON d.specialist_id = s.specialist_id
        WHERE d.doctor_id = ?
        GROUP BY d.doctor_id;
    `;

    connection.query(query, [doctor_id], async (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ" });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bác sĩ" });
        }

        return res.status(200).json({
            message: "Success",
            data: results[0]

        });
    });
};

const all_appointments = async (req, res) => {
    const { patient_id } = req.body; // Lấy doctor_id từ body request

    if (!patient_id) {
        return res.status(400).json({ message: "patient_id trống" });
    }

    const query = `
        SELECT * FROM Appointments 
        WHERE patient_id = ?
    `;

    connection.query(query, [patient_id], async (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ" });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bệnh nhân" });
        }

        return res.status(200).json({
            message: "Success",
            data: results

        });
    });
};

const create_appointment = (req, res) => {
    const { patient_id, doctor_id, appointment_date, appointment_time, status, reason, is_deposit, consultation_fee, is_online } = req.body;

    const formattedTime = appointment_time.length === 5 ? appointment_time + ':00' : appointment_time;
    const appointment_datetime = `${appointment_date} ${formattedTime}`;

    // 1. Kiểm tra xem có cuộc hẹn nào với bác sĩ và thời gian đó có status = 'Cancelled' không
    const checkCancelledQuery = `
        SELECT * 
        FROM Appointments 
        WHERE doctor_id = ? AND appointment_datetime = ? AND status = 'Cancelled'
    `;

    connection.query(checkCancelledQuery, [doctor_id, appointment_datetime], (checkErr, checkResults) => {
        if (checkErr) {
            console.error('Lỗi khi kiểm tra cuộc hẹn:', checkErr);
            return res.status(500).json({ error: 'Lỗi máy chủ' });
        }

        // Nếu có cuộc hẹn bị huỷ, cho phép người dùng khác đặt lịch
        if (checkResults.length > 0) {
            // 2. Tạo cuộc hẹn mới
            const insertQuery = `
                INSERT INTO Appointments 
                (patient_id, doctor_id, appointment_datetime, status, reason, is_deposit, consultation_fee, is_online)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            `;

            const values = [
                patient_id,
                doctor_id,
                appointment_datetime,
                status,
                reason,
                is_deposit,
                consultation_fee,
                is_online
            ];

            connection.query(insertQuery, values, (insertErr, results) => {
                if (insertErr) {
                    console.error('Lỗi khi thêm cuộc hẹn:', insertErr);
                    return res.status(500).json({ error: 'Lỗi máy chủ' });
                }

                res.status(201).json({ message: 'Success' });
            });
        } else {
            // Nếu không có cuộc hẹn bị huỷ, kiểm tra xem có cuộc hẹn khác đã tồn tại tại thời gian đó không
            const countQuery = `
                SELECT COUNT(*) AS count
                FROM Appointments 
                WHERE doctor_id = ? AND appointment_datetime = ? AND status != 'Cancelled'
            `;

            connection.query(countQuery, [doctor_id, appointment_datetime], (countErr, countResults) => {
                if (countErr) {
                    console.error('Lỗi khi kiểm tra cuộc hẹn:', countErr);
                    return res.status(500).json({ error: 'Lỗi máy chủ' });
                }

                const currentCount = countResults[0].count;

                // Nếu có cuộc hẹn tồn tại, không cho phép thêm cuộc hẹn mới
                if (currentCount > 0) {
                    return res.status(200).json({ message: 'Fail', reason: 'Đã có cuộc hẹn khác tại thời gian này' });
                }

                // 3. Tạo cuộc hẹn mới nếu không có cuộc hẹn tồn tại
                const insertQuery = `
                    INSERT INTO Appointments 
                    (patient_id, doctor_id, appointment_datetime, status, reason, is_deposit, consultation_fee, is_online)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                `;

                const values = [
                    patient_id,
                    doctor_id,
                    appointment_datetime,
                    status,
                    reason,
                    is_deposit,
                    consultation_fee,
                    is_online
                ];

                connection.query(insertQuery, values, (insertErr, results) => {
                    if (insertErr) {
                        console.error('Lỗi khi thêm cuộc hẹn:', insertErr);
                        return res.status(500).json({ error: 'Lỗi máy chủ' });
                    }

                    res.status(201).json({ message: 'Success' });
                });
            });
        }
    });
};


const check_doctor_appointment = (req, res) => {
    const { doctor_id } = req.body;

    if (!doctor_id) {
        return res.status(400).json({ message: "doctor_id trống" });
    }

    const query = `
        SELECT appointment_datetime FROM Appointments 
        WHERE doctor_id = ? AND is_online = true
    `;

    connection.query(query, [doctor_id], (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ" });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bác sĩ" });
        }

        // Chuyển sang múi giờ Việt Nam
        const converted = results.map(r => ({
            appointment_datetime: moment(r.appointment_datetime).tz("Asia/Ho_Chi_Minh").format("YYYY-MM-DD HH:mm:ss")
        }));

        return res.status(200).json({
            message: "Success",
            data: converted
        });
    });
}

module.exports = {
    doctor_detail,
    all_appointments,
    create_appointment,
    check_doctor_appointment
}