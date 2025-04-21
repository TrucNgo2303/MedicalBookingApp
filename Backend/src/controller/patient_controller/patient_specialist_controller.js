const connection = require('../../config/database')

const get_all_specialists = async (req, res) => {
    const sql = `SELECT 
                    s.specialist_id, 
                    s.specialist_name,
                    s.summary,
                    s.icon,
                    CONCAT(FORMAT(s.consultation_fee, 0), ' VNĐ') AS consultation_fee,
                    COUNT(d.doctor_id) AS numberOfDoctor
                FROM Specialists s
                LEFT JOIN Doctors d ON s.specialist_id = d.specialist_id
                GROUP BY s.specialist_id, s.specialist_name, s.consultation_fee;`

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
    });
};

const get_specialist = async (req, res) => {

    const { specialist_id } = req.body;

    if (!specialist_id) {
        return res.status(400).json({ error: "Thiếu specialist_id" });
    }

    const sql = `SELECT
                    s.specialist_id, 
                    s.specialist_name,
                    s.summary,
                    s.icon,
                    CONCAT(FORMAT(s.consultation_fee, 0), ' VNĐ') AS consultation_fee
                FROM Specialists s
                WHERE specialist_id = ?`

    connection.query(sql, [specialist_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy chuyên khoa" });
        }
        res.json({
            message: 'Success',
            data: result[0]
        });
    });
};


const get_all_doctors_in_specialists = async (req, res) => {
    const { specialist_id } = req.body;

    if (!specialist_id) {
        return res.status(400).json({ error: "Thiếu specialist_id" });
    }

    const sql = `SELECT 
                    d.doctor_id,
                    d.full_name,
                    d.specialty,
                    d.qualification,
                    d.avatar,
                    ROUND(AVG(c.star), 1) AS average_star
                FROM Doctors d
                LEFT JOIN Comments c ON d.doctor_id = c.doctor_id
                WHERE d.specialist_id = ? 
                GROUP BY d.doctor_id;`

    connection.query(sql, [specialist_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ error: "Không tìm thấy bác sĩ" });
        }
        res.json({
            message: "Success",
            data: result
        });
    });
};


module.exports = {
    get_all_specialists,
    get_specialist,
    get_all_doctors_in_specialists
}