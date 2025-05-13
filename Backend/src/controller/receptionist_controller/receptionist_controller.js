const connection = require('../../config/database')
const moment = require('moment-timezone');

const get_online_appointments = (req, res) => {

    query = `
        SELECT 
            a.appointment_id,
            a.appointment_datetime,
            a.status,
            a.reason,
            a.consultation_fee,
            a.is_paid,
            a.is_deposit,
            p.full_name AS patient_name,
            p.phone_number AS patient_phone_number,
            d.full_name AS doctor_name,
            d.specialty AS doctor_specialty
        FROM Appointments a
        JOIN Patients p ON a.patient_id = p.patient_id
        JOIN Doctors d ON a.doctor_id = d.doctor_id
        WHERE a.is_online = TRUE
        AND DATE(a.appointment_datetime) = CURRENT_DATE;
    `

    connection.query(query, (err, result) => {
        if (err) {
            console.log(err);
            return res.status(500).json({ error: 'Lỗi server' });
        }

        // Format the appointment_datetime to the desired format
        const formattedResult = result.map(appointment => ({
            ...appointment,
            appointment_datetime: moment(appointment.appointment_datetime).tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD HH:mm:ss')
        }));

        return res.status(200).json(formattedResult);
    });
}
const get_online_appointments_detail = (req, res) => {
    const { appointment_id } = req.body;

    if (!appointment_id) {
        return res.status(400).json({ error: 'Appointment ID is required' });
    }

    const query = `
        SELECT 
            a.appointment_id,
            a.appointment_datetime,
            a.status,
            a.reason,
            a.consultation_fee,
            a.is_paid,
            a.is_deposit,
            p.patient_id AS patient_id,
            p.full_name AS patient_name,
            p.phone_number AS patient_phone_number,
            d.doctor_id AS doctor_id,
            d.full_name AS doctor_name,
            d.specialty AS doctor_specialty
        FROM Appointments a
        JOIN Patients p ON a.patient_id = p.patient_id
        JOIN Doctors d ON a.doctor_id = d.doctor_id
        WHERE a.appointment_id = ?
    `;

    connection.query(query, [appointment_id], (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ error: 'Lỗi server' });
        }

        if (result.length === 0) {
            return res.status(404).json({ error: 'Appointment not found' });
        }

        const appointment = {
            ...result[0],
            appointment_datetime: moment(result[0].appointment_datetime)
                .tz('Asia/Ho_Chi_Minh')
                .format('YYYY-MM-DD HH:mm:ss')
        };

        return res.status(200).json(appointment);
    });
};

const payment_status_update = (req, res) => {
    const { appointment_id } = req.body;

    if (!appointment_id) {
        return res.status(400).json({ error: 'Appointment ID are required' });
    }

    const query = `
        UPDATE Appointments
        SET is_paid = true
        WHERE appointment_id = ?
    `;

    connection.query(query, [appointment_id], (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ error: 'Lỗi server' });
        }

        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Không tìm thấy Appointment' });
        }

        return res.status(200).json({ message: 'Success' });
    });

}

const add_to_waiting_list = (req, res) => {
    const { patient_id, doctor_id, priority, appointment_id } = req.body;

    if (!patient_id || !doctor_id || !priority || !appointment_id) {
        return res.status(400).json({ error: 'Patient ID, Doctor ID, Priority và Appointment ID bị thiếu' });
    }

    const checkQuery = `
        SELECT * FROM Waiting_list WHERE appointment_id = ?
    `;

    connection.query(checkQuery, [appointment_id], (err, results) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ error: 'Lỗi server khi kiểm tra dữ liệu' });
        }

        if (results.length > 0) {
            return res.status(400).json({ error: 'Appointment ID đã có trong danh sách chờ' });
        }

        const insertQuery = `
            INSERT INTO Waiting_list (patient_id, doctor_id, priority, appointment_id)
            VALUES (?, ?, ?, ?)
        `;

        connection.query(insertQuery, [patient_id, doctor_id, priority, appointment_id], (err, result) => {
            if (err) {
                console.error(err);
                return res.status(500).json({ error: 'Lỗi server khi thêm vào danh sách chờ' });
            }

            return res.status(200).json({ message: 'Success' });
        });
    });
};

const check_phone_number = (req, res) => {
    const { phone_number } = req.body;

    if (!phone_number) {
        return res.status(400).json({ error: 'Số điện thoại bị thiếu' });
    }

    const query = `
        SELECT * FROM Patients WHERE phone_number = ?
    `;

    connection.query(query, [phone_number], (err, results) => {
        if (err) {
            console.error("Lỗi khi kiểm tra số điện thoại:", err);
            return res.status(500).json({ error: 'Lỗi server khi kiểm tra số điện thoại' });
        }

        if (results.length > 0) {
            const patient = results[0];
            return res.status(200).json({
                message: 'Phone number is exist',
                data: patient
            });
        } else {
            return res.status(404).json({ message: 'Phone number is not exist' });
        }
    });
};

const add_customer_offline = (req, res) => {
    const { patient_id, doctor_id, reason, consultation_fee, is_paid } = req.body;

    if (!patient_id || !doctor_id || !consultation_fee || is_paid === undefined) {
        return res.status(400).json({ error: 'Patient ID, Doctor ID, Consultation Fee và Is Paid bị thiếu' });
    }

    // Lấy thời gian hiện tại theo múi giờ Việt Nam và làm tròn xuống :00 hoặc :30
    let now = moment().tz('Asia/Ho_Chi_Minh');
    const minutes = now.minutes();
    const roundedMinutes = minutes < 30 ? 0 : 30;
    const appointmentDatetime = now.minutes(roundedMinutes).seconds(0).milliseconds(0).format('YYYY-MM-DD HH:mm:ss');

    // Thực hiện thêm dữ liệu vào bảng Appointments
    const insertAppointmentQuery = `
        INSERT INTO Appointments (patient_id, doctor_id, appointment_datetime, status, reason, consultation_fee, is_paid, is_online)
        VALUES (?, ?, ?, 'Confirmed', ?, ?, ?, false)
    `;

    connection.query(insertAppointmentQuery, [patient_id, doctor_id, appointmentDatetime, reason, consultation_fee, is_paid], (err, result) => {
        if (err) {
            console.error('Lỗi khi thêm cuộc hẹn:', err);
            return res.status(500).json({ error: 'Lỗi khi thêm cuộc hẹn' });
        }

        const appointment_id = result.insertId;

        // Tiếp tục thêm dữ liệu vào bảng Waiting_list
        const insertWaitingListQuery = `
            INSERT INTO Waiting_list (patient_id, doctor_id, appointment_id, status)
            VALUES (?, ?, ?, 'waiting')
        `;

        connection.query(insertWaitingListQuery, [patient_id, doctor_id, appointment_id], (err2, result2) => {
            if (err2) {
                console.error('Lỗi khi thêm vào danh sách chờ:', err2);
                return res.status(500).json({ error: 'Lỗi khi thêm vào danh sách chờ' });
            }

            return res.status(200).json({ message: 'Success', appointment_id });
        });
    });
};


const all_specialist = (req, res) => {
    const query = `
        SELECT * FROM Specialists
    `;

    connection.query(query, (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ error: 'Lỗi server' });
        }

        return res.status(200).json(result);
    });
}

const check_doctor = (req, res) => {
    const { specialist_name } = req.body;

    if (!specialist_name) {
        return res.status(400).json({ error: 'specialist_name is required' });
    }

    const query = `
        SELECT d.doctor_id, d.full_name, COUNT(wl.waiting_list_id) AS waiting_count
        FROM Doctors d
        LEFT JOIN Waiting_list wl ON d.doctor_id = wl.doctor_id AND wl.status = 'waiting'
        WHERE d.specialty = ?
        GROUP BY d.doctor_id, d.full_name
    `;

    connection.query(query, [specialist_name], (err, results) => {
        if (err) {
            console.error("Lỗi truy vấn:", err);
            return res.status(500).json({ error: 'Lỗi server khi kiểm tra bác sĩ' });
        }

        return res.status(200).json({
            message: 'Success',
            data: results
        });
    });
};


const add_offline_patient = (req, res) => {
    const { full_name, phone_number, address } = req.body;

    if (!full_name || !phone_number || !address) {
        return res.status(400).json({ error: 'Full name, phone number và address bị thiếu' });
    }

    // Kiểm tra xem số điện thoại đã tồn tại chưa
    const checkQuery = `SELECT patient_id FROM Patients WHERE phone_number = ?`;

    connection.query(checkQuery, [phone_number], (err, results) => {
        if (err) {
            console.error("Lỗi khi kiểm tra số điện thoại:", err);
            return res.status(500).json({ error: 'Lỗi server khi kiểm tra số điện thoại' });
        }

        if (results.length > 0) {
            return res.status(409).json({ error: 'Số điện thoại đã tồn tại' }); // Conflict
        }

        // Nếu chưa tồn tại thì thêm mới
        const insertQuery = `
            INSERT INTO Patients (full_name, phone_number, address)
            VALUES (?, ?, ?)
        `;

        connection.query(insertQuery, [full_name, phone_number, address], (err, result) => {
            if (err) {
                console.error("Lỗi khi thêm bệnh nhân:", err);
                return res.status(500).json({ error: 'Lỗi server khi thêm bệnh nhân' });
            }

            return res.status(200).json({ message: 'Success', patient_id: result.insertId });
        });
    });
};


module.exports = {
    get_online_appointments,
    get_online_appointments_detail,
    payment_status_update,
    add_to_waiting_list,
    check_phone_number,
    add_customer_offline,
    all_specialist,
    check_doctor,
    add_offline_patient
}