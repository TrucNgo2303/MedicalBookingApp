const connection = require('../../config/database')
const moment = require('moment-timezone');

const appointment_detail = async (req, res) => {
    const { appointment_id } = req.body;

    if (!appointment_id) {
        return res.status(400).json({ message: 'Appointment ID is required' });
    }

    const query = `
        SELECT
            p.patient_id,
            p.avatar AS patient_avatar,
            p.full_name AS patient_name,
            d.doctor_id,
            d.full_name AS doctor_name,
            d.specialty,
            a.appointment_datetime,
            a.reason,
            a.status,
            a.is_online,
            a.consultation_fee,
            EXISTS (
                SELECT 1 FROM Preliminary_Diagnoses pd
                WHERE pd.appointment_id = a.appointment_id
            ) AS has_preliminary_diagnosis,
            EXISTS (
                SELECT 1 FROM Prescriptions pr
                WHERE pr.appointment_id = a.appointment_id
            ) AS has_prescription
        FROM Appointments a
        JOIN Patients p ON a.patient_id = p.patient_id
        JOIN Doctors d ON a.doctor_id = d.doctor_id
        WHERE a.appointment_id = ?;
    `;

    connection.query(query, [appointment_id], (error, results) => {
        if (error) {
            console.error('Error executing query:', error);
            return res.status(500).json({ message: 'Internal server error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        const result = results[0];

        // Chuyển appointment_datetime sang giờ Việt Nam
        const appointmentTimeVN = moment(result.appointment_datetime)
            .tz('Asia/Ho_Chi_Minh');

        // Tách date và time theo định dạng
        result.appointment_date = appointmentTimeVN.format('YYYY-MM-DD');
        result.appointment_time = appointmentTimeVN.format('HH:mm:ss');

        // Xoá trường gốc nếu không muốn trả về
        delete result.appointment_datetime;

        return res.status(200).json(result);
    });
};

module.exports = {
    appointment_detail,

}