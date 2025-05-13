const connection = require('../../config/database')
const moment = require('moment-timezone');

const number_of_appointment_today = async (req, res) => {
    const authorization_id = req.user?.authorization_id;

    if (!authorization_id) {
        return res.status(401).json({ message: "Không tìm thấy authorization_id từ token" });
    }

    // Truy vấn doctor_id từ authorization_id
    const getDoctorIdQuery = `
        SELECT doctor_id FROM Doctors WHERE authorization_id = ?
    `;

    connection.query(getDoctorIdQuery, [authorization_id], (err, doctorResults) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu (Doctors):", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ khi tìm doctor_id" });
        }

        if (doctorResults.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bác sĩ tương ứng" });
        }

        const doctor_id = doctorResults[0].doctor_id;
        console.log("doctor_id", doctor_id);

        // Truy vấn số lịch hẹn hôm nay
        const appointmentQuery = `
            SELECT COUNT(*) AS number_of_appointment_today, doctor_id
            FROM Appointments 
            WHERE doctor_id = ? AND DATE(appointment_datetime) = CURDATE() AND status = 'Confirmed'
        `;

        connection.query(appointmentQuery, [doctor_id], (err, appointmentResults) => {
            if (err) {
                console.error("Lỗi truy vấn cơ sở dữ liệu (Appointments):", err.message);
                return res.status(500).json({ message: "Lỗi máy chủ khi lấy lịch hẹn" });
            }

            return res.status(200).json({
                message: "Success",
                data: appointmentResults[0]
            });
        });
    });
};

const today_appointment = (req, res) => {
    const authorization_id = req.user?.authorization_id;

    if (!authorization_id) {
        return res.status(401).json({ message: "Không tìm thấy authorization_id từ token" });
    }

    const getDoctorIdQuery = `
        SELECT doctor_id FROM Doctors WHERE authorization_id = ?
    `;

    connection.query(getDoctorIdQuery, [authorization_id], (err, doctorResults) => {
        if (err) {
            console.error("Lỗi truy vấn cơ sở dữ liệu (Doctors):", err.message);
            return res.status(500).json({ message: "Lỗi máy chủ khi tìm doctor_id" });
        }

        if (doctorResults.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy bác sĩ tương ứng" });
        }

        const doctor_id = doctorResults[0].doctor_id;

        const todayVN = moment().tz("Asia/Ho_Chi_Minh").format("YYYY-MM-DD");

        const appointmentQuery = `
            SELECT 
                ap.appointment_id, 
                ap.doctor_id, 
                ap.patient_id, 
                ap.appointment_datetime, 
                ap.status, 
                p.avatar, 
                p.full_name, 
                ap.is_online
            FROM Appointments ap
            JOIN Patients p ON ap.patient_id = p.patient_id
            JOIN Waiting_list wl ON ap.appointment_id = wl.appointment_id
            WHERE 
                ap.doctor_id = ? 
                AND DATE(ap.appointment_datetime) = ? 
                AND ap.status = 'Confirmed'
                AND wl.status = 'waiting'
            
        `;

        connection.query(appointmentQuery, [doctor_id, todayVN], (err, results) => {
            if (err) {
                console.error("Lỗi truy vấn cơ sở dữ liệu (Appointments):", err.message);
                return res.status(500).json({ message: "Lỗi máy chủ khi lấy lịch hẹn" });
            }

            const converted = results.map(r => ({
                ...r,
                appointment_datetime: moment(r.appointment_datetime)
                    .tz("Asia/Ho_Chi_Minh")
                    .format("YYYY-MM-DD HH:mm:ss")
            }));

            return res.status(200).json({
                message: "Success",
                data: converted
            });
        });
    });
};


module.exports = {
    number_of_appointment_today,
    today_appointment
};