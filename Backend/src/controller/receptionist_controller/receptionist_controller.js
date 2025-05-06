const connection = require('../../config/database')
const moment = require('moment-timezone');

const get_all_appointment_today = async (req, res) => {

    const query = `
    SELECT * 
    FROM Appointments
    WHERE DATE(appointment_datetime) = CURDATE();
    `

    connection.query(query, async (err, results) => {

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
    })
};

const check_doctor_schedule = (req, res) => {
    const { specialist_name, appointment_time } = req.body;

    if (!specialist_name || !appointment_time) {
        return res.status(400).json({ message: "Thiếu thông tin" });
    }

    // Lấy ngày hôm nay theo giờ Việt Nam
    const currentDateVN = moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD');

    // Tạo appointment_datetime
    const appointment_datetime = `${currentDateVN} ${appointment_time}`;

    // Câu SQL
    const query = `
        SELECT d.doctor_id, d.full_name
        FROM Doctors d
        JOIN Specialists s ON d.specialist_id = s.specialist_id
        WHERE s.specialist_name = ?
        AND d.doctor_id NOT IN (
            SELECT a.doctor_id
            FROM Appointments a
            WHERE a.appointment_datetime = ?
            AND a.status != 'Cancelled'
        );
    `;

    connection.query(query, [specialist_name, appointment_datetime], (error, results) => {
        if (error) {
            console.error("Lỗi truy vấn:", error);
            return res.status(500).json({ message: "Lỗi server" });
        }

        return res.status(200).json({
            message: "Success",
            data: results
        });
    });
};

const add_waiting = (req, res) => {
    const { patient_name, phone_number, address, date_of_birth, doctor_id, request_time } = req.body;

    if (!patient_name || !phone_number || !address || !date_of_birth || !doctor_id || !request_time) {
        return res.status(400).json({ message: "Thiếu thông tin" });
    }

    // Lấy ngày hôm nay theo múi giờ Việt Nam
    const vnDate = moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD');
    const requested_datetime = `${vnDate} ${request_time}:00`; // "2025-04-25 10:30:00"

    const checkQuery = `SELECT patient_id FROM Patients WHERE full_name = ? AND phone_number = ?`;
    connection.query(checkQuery, [patient_name, phone_number], (err, result) => {
        if (err) return res.status(500).json({ message: 'Lỗi truy vấn kiểm tra bệnh nhân', error: err });

        let patientId = result.length > 0 ? result[0].patient_id : null;

        if (!patientId) {
            const insertPatientQuery = `
                INSERT INTO Patients (full_name, phone_number, address, date_of_birth)
                VALUES (?, ?, ?, ?)
            `;
            connection.query(insertPatientQuery, [patient_name, phone_number, address, date_of_birth], (err, insertResult) => {
                if (err) return res.status(500).json({ message: 'Lỗi thêm bệnh nhân', error: err });

                patientId = insertResult.insertId;

                const insertWaitingQuery = `
                    INSERT INTO Waiting_list (patient_id, doctor_id, requested_datetime)
                    VALUES (?, ?, ?)
                `;
                connection.query(insertWaitingQuery, [patientId, doctor_id, requested_datetime], (err) => {
                    if (err) return res.status(500).json({ message: 'Lỗi thêm vào danh sách chờ', error: err });

                    res.status(200).json({ message: 'Thêm vào danh sách chờ thành công' });
                });
            });
        } else {
            const insertWaitingQuery = `
                INSERT INTO Waiting_list (patient_id, doctor_id, requested_datetime)
                VALUES (?, ?, ?)
            `;
            connection.query(insertWaitingQuery, [patientId, doctor_id, requested_datetime], (err) => {
                if (err) return res.status(500).json({ message: 'Lỗi thêm vào danh sách chờ', error: err });

                res.status(200).json({ message: 'Thêm vào danh sách chờ thành công' });
            });
        }
    });
};


module.exports = {
    get_all_appointment_today,
    check_doctor_schedule,
    add_waiting
}