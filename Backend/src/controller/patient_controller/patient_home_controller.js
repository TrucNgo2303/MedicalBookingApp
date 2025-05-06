const connection = require('../../config/database')
const moment = require('moment-timezone');
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
    const sql = `SELECT specialist_id, specialist_name, icon FROM Specialists`

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
    const sqlDoctors = `SELECT doctor_id, full_name FROM Doctors`;
    const sqlSpecialists = `SELECT specialist_id, specialist_name FROM Specialists`; // Bỏ DISTINCT, dùng GROUP BY nếu cần

    connection.query(sqlDoctors, (errDoctors, doctors) => {
        if (errDoctors) {
            return res.status(500).json({ error: "Lỗi truy vấn database (Doctors)" });
        }

        connection.query(sqlSpecialists, (errSpecialists, specialists) => {
            if (errSpecialists) {
                return res.status(500).json({ error: "Lỗi truy vấn database (Specialists)" });
            }

            // Format danh sách bác sĩ
            const doctorList = doctors.map(doc => ({
                doctor_id: doc.doctor_id,
                specialist_id: null,
                name: `Bs. ${doc.full_name}`
            }));

            // Format danh sách chuyên khoa
            const specialistList = specialists.map(spec => ({
                doctor_id: null,
                specialist_id: spec.specialist_id,
                name: `Ck. ${spec.specialist_name}`
            }));

            const responseData = [...doctorList, ...specialistList];

            res.json({
                message: "Success",
                data: responseData
            });
        });
    });
};


const get_appointment_status = (req, res) => {
    const { authorization_id } = req.user;
    const { status } = req.body;

    // Lấy patient_id từ authorization_id
    const getPatientQuery = `
        SELECT patient_id, full_name, avatar 
        FROM Patients 
        WHERE authorization_id = ?
    `;

    connection.query(getPatientQuery, [authorization_id], (err, patientResult) => {
        if (err) return res.status(500).json({ message: 'Lỗi truy vấn patient', error: err });
        if (patientResult.length === 0) {
            return res.status(404).json({ message: 'Không tìm thấy bệnh nhân' });
        }

        const patient_id = patientResult[0].patient_id;

        // Truy vấn danh sách cuộc hẹn và thay patient_name, patient_avatar bằng doctor_name, doctor_avatar
        const appointmentQuery = `
            SELECT 
                a.appointment_id,
                a.doctor_id,
                a.appointment_datetime,
                d.avatar AS doctor_avatar,      -- Thay patient_avatar thành doctor_avatar
                d.full_name AS doctor_name,     -- Thay patient_name thành doctor_name
                s.specialist_name AS specialist,
                a.consultation_fee
            FROM Appointments a
            JOIN Doctors d ON a.doctor_id = d.doctor_id
            LEFT JOIN Specialists s ON d.specialist_id = s.specialist_id
            WHERE a.patient_id = ? AND a.status = ?
            ORDER BY a.appointment_datetime DESC
        `;

        connection.query(
            appointmentQuery,
            [patient_id, status],
            (err, appointmentResults) => {
                if (err) return res.status(500).json({ message: 'Lỗi truy vấn appointments', error: err });

                // Chỉnh sửa appointment_datetime theo giờ Việt Nam (GMT+7)
                appointmentResults.forEach(appointment => {
                    appointment.appointment_datetime = moment(appointment.appointment_datetime)
                        .tz('Asia/Ho_Chi_Minh', true) // Chuyển đổi về múi giờ Việt Nam
                        .format('YYYY-MM-DD HH:mm:ss'); // Định dạng thời gian
                });

                return res.status(200).json({
                    message: 'Success',
                    data: appointmentResults
                });
            }
        );
    });
};

module.exports = {
    patient_info,
    specialists,
    top_doctors,
    search_result,
    get_appointment_status
}