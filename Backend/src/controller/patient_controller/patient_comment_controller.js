const connection = require('../../config/database')

const get_all_comments = async (req, res) => {
    const { doctor_id } = req.body;

    if (!doctor_id) {
        return res.status(400).json({ error: "Thiếu doctor_id" });
    }

    const sql = `SELECT
                    c.comment_id,
                    c.comment_detail,
                    c.star,
                    c.created_at,
                    p.full_name AS patient_name,
                    p.avatar AS patient_avatar
                FROM Comments c
                JOIN Patients p ON c.patient_id = p.patient_id
                WHERE c.doctor_id = ?;`

    connection.query(sql, [doctor_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bình luận" });
        }
        res.json({
            message: 'Success',
            data: result
        });
    });
}

const get_all_reply = async (req, res) => {
    const { comment_id } = req.body;

    if (!comment_id) {
        return res.status(400).json({ error: "Thiếu comment_id" });
    }

    const sql = `SELECT
                    r.reply_id,
                    r.reply_detail,
                    r.user_type,
                    COALESCE(p.full_name, d.full_name, a.full_name) AS user_name,
                    COALESCE(p.avatar, d.avatar, a.avatar) AS user_avatar
                FROM CommentReplies r
                LEFT JOIN Patients p ON r.user_type = 'Patient' AND r.user_id = p.patient_id
                LEFT JOIN Doctors d ON r.user_type = 'Doctor' AND r.user_id = d.doctor_id
                LEFT JOIN Admins a ON r.user_type = 'Admin' AND r.user_id = a.admin_id
                WHERE r.comment_id = ?;`

    connection.query(sql, [comment_id], (err, result) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database" });
        }
        if (result.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bình luận" });
        }
        res.json({
            message: 'Success',
            data: result
        });
    });
}

const add_comment = async (req, res) => {
    const { doctor_id, comment_detail, star } = req.body;
    const authorization_id = req.user?.authorization_id;

    if (!authorization_id) {
        return res.status(401).json({ error: "Không xác định được người dùng" });
    }

    if (!doctor_id || !comment_detail || !star) {
        return res.status(400).json({ error: "Thiếu thông tin" });
    }

    const getPatientIdSql = `SELECT patient_id FROM Patients WHERE authorization_id = ?`;
    connection.query(getPatientIdSql, [authorization_id], (err, results) => {
        if (err) {
            return res.status(500).json({ error: "Lỗi truy vấn database khi lấy patient_id" });
        }

        if (results.length === 0) {
            return res.status(404).json({ error: "Không tìm thấy bệnh nhân" });
        }

        const patient_id = results[0].patient_id;
        console.log(patient_id)

        const insertCommentSql = `INSERT INTO Comments (doctor_id, patient_id, comment_detail, star)
                                  VALUES (?, ?, ?, ?)`;

        connection.query(insertCommentSql, [doctor_id, patient_id, comment_detail, star], (err, result) => {
            if (err) {
                return res.status(500).json({ error: "Lỗi truy vấn database khi thêm bình luận" });
            }

            res.json({
                message: 'Success'
            });
        });
    });
};


const add_reply = async (req, res) => {
    try {
        const { comment_id, reply_detail } = req.body;
        const authorization_id = req.user?.authorization_id; // Lấy từ token

        if (!comment_id || !reply_detail) {
            return res.status(400).json({ error: "Thiếu comment_id hoặc nội dung trả lời." });
        }

        if (!authorization_id) {
            return res.status(401).json({ error: "Không thể xác định người dùng từ token." });
        }

        // Truy vấn để lấy role từ bảng Authorizations
        const getUserTypeSql = `SELECT role FROM Authorizations WHERE authorization_id = ?`;

        connection.query(getUserTypeSql, [authorization_id], (err, userTypeResult) => {
            if (err) {
                return res.status(500).json({ error: "Lỗi khi truy vấn vai trò người dùng." });
            }

            if (userTypeResult.length === 0) {
                return res.status(404).json({ error: "Không tìm thấy thông tin người dùng." });
            }

            const user_type = userTypeResult[0].role;

            // Thêm phản hồi vào bảng CommentReplies
            const insertReplySql = `INSERT INTO CommentReplies (comment_id, user_type, user_id, reply_detail)
                                    VALUES (?, ?, ?, ?)`;

            connection.query(insertReplySql, [comment_id, user_type, authorization_id, reply_detail], (err, result) => {
                console.log(comment_id)
                console.log(user_type)
                console.log(authorization_id)
                console.log(reply_detail)
                if (err) {
                    return res.status(500).json({ error: "Lỗi khi thêm phản hồi vào cơ sở dữ liệu." });
                }

                res.json({
                    message: "Success",
                    data: result
                });
            });
        });
    } catch (error) {
        res.status(500).json({ error: "Đã xảy ra lỗi máy chủ." });
    }
};


module.exports = {
    get_all_comments,
    get_all_reply,
    add_comment,
    add_reply
}