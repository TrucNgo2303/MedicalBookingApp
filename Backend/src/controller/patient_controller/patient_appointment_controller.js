const connection = require('../../config/database')
require('dotenv').config()


const doctor_detail = async (req, res) => {
    const { doctor_id } = req.body; // Lấy doctor_id từ body request

    if (!doctor_id) {
        return res.status(400).json({ message: "Doctor_id trống" });
    }

    const query = `
        SELECT d.doctor_id, d.full_name, d.phone_number, d.specialty, d.qualification, d.experience_years, d.summary, d.avatar, d.patient_count, 
               ROUND(COALESCE(AVG(c.star), 0), 1) AS average_star, 
               COUNT(c.comment_id) AS total_comments
        FROM Doctors d
        LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
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

module.exports = {
    doctor_detail,
    all_appointments
}