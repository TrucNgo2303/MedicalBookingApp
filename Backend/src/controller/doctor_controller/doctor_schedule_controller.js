const connection = require('../../config/database')

const doctor_schedule = async (req, res) => {
    const { authorization_id } = req.user;
    const { appointment_date } = req.body;
    if (!authorization_id) {
        return res.status(401).json({ message: "Không tìm thấy authorization_id từ token" });
    }
    if (!appointment_date) {
        return res.status(400).json({ message: "Không tìm thấy appointment_date từ body" });
    }

    query = `
        SELECT 
            a.appointment_id,
            d.doctor_id,
            p.patient_id,
            a.appointment_datetime,
            a.status,
            p.avatar,
            p.full_name,
            a.is_online
        FROM Appointments a
        INNER JOIN doctors d ON d.doctor_id = a.doctor_id
        INNER JOIN patients p ON p.patient_id = a.patient_id
        WHERE d.authorization_id = ?
        AND DATE(a.appointment_datetime) = ?
        ORDER BY a.appointment_datetime ASC
    `

    connection.query(query, [authorization_id, appointment_date], (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu:", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ khi lấy lịch hẹn" });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy lịch hẹn nào" });
        }

        return res.status(200).json({
            message: "Success",
            data: results
        });
    });
}



module.exports = {
    doctor_schedule
}