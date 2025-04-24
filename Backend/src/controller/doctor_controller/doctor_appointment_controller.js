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

const preliminary_and_prescriptions = (req, res) => {
    const { appointment_id } = req.body;

    if (!appointment_id) {
        return res.status(400).json({ error: 'appointment_id is required' });
    }

    const queryPreliminary = `
        SELECT diagnosis_id, appointment_id, doctor_id, preliminary_diagnosis
        FROM Preliminary_Diagnoses
        WHERE appointment_id = ?
    `;

    connection.query(queryPreliminary, [appointment_id], (err, preliminaryDiagnoses) => {
        if (err) {
            console.error('Lỗi lấy chẩn đoán sơ bộ:', err);
            return res.status(500).json({ error: 'Lỗi lấy chẩn đoán sơ bộ' });
        }

        const queryFinal = `
            SELECT conclusion_id, appointment_id, doctor_id, final_conclusion, recommendations
            FROM Final_Conclusions
            WHERE appointment_id = ?
        `;

        connection.query(queryFinal, [appointment_id], (err, finalConclusions) => {
            if (err) {
                console.error('Lỗi lấy kết luận cuối cùng:', err);
                return res.status(500).json({ error: 'Lỗi lấy kết luận cuối cùng' });
            }

            const queryPrescription = `
                SELECT prescription_id, appointment_id, doctor_id, patient_id
                FROM Prescriptions
                WHERE appointment_id = ?
            `;

            connection.query(queryPrescription, [appointment_id], (err, prescriptions) => {
                if (err) {
                    console.error('Lỗi lấy đơn thuốc:', err);
                    return res.status(500).json({ error: 'Lỗi lấy đơn thuốc' });
                }

                if (prescriptions.length === 0) {
                    // Không có đơn thuốc
                    return res.status(200).json({
                        message: "Success",
                        data: {
                            appointment_id,
                            preliminary_diagnosis: preliminaryDiagnoses[0]?.preliminary_diagnosis || null,
                            final_conclusion: finalConclusions[0]?.final_conclusion || null,
                            recommendations: finalConclusions[0]?.recommendations || null,
                            prescriptionDetails: []
                        }
                    });
                }

                // Lấy chi tiết của tất cả các đơn thuốc
                const prescriptionDetailsPromises = prescriptions.map(prescription => {
                    return new Promise((resolve, reject) => {
                        const queryDetails = `
                            SELECT detail_id, prescription_id, medicine_name, dosage, usage_instruction, duration
                            FROM Prescription_Details
                            WHERE prescription_id = ?
                        `;
                        connection.query(queryDetails, [prescription.prescription_id], (err, details) => {
                            if (err) {
                                return reject(err);
                            }
                            resolve(details);
                        });
                    });
                });

                // Chờ tất cả các truy vấn chi tiết đơn thuốc hoàn tất
                Promise.all(prescriptionDetailsPromises)
                    .then(allDetails => {
                        // Gộp tất cả chi tiết đơn thuốc vào một mảng
                        const prescriptionDetails = allDetails.flat();

                        res.status(200).json({
                            message: "Success",
                            data: {
                                appointment_id,
                                preliminary_diagnosis: preliminaryDiagnoses[0]?.preliminary_diagnosis || null,
                                final_conclusion: finalConclusions[0]?.final_conclusion || null,
                                recommendations: finalConclusions[0]?.recommendations || null,
                                prescriptionDetails: prescriptionDetails || []
                            }
                        });
                    })
                    .catch(err => {
                        console.error('Lỗi lấy chi tiết đơn thuốc:', err);
                        res.status(500).json({ error: 'Lỗi lấy chi tiết đơn thuốc' });
                    });
            });
        });
    });
};

const add_prescriptions = (req, res) => {
    const { appointment_id, medicine_name, dosage, usage_instruction, duration } = req.body;

    if (!appointment_id || !medicine_name || !dosage || !usage_instruction || !duration) {
        return res.status(400).json({ error: 'Thiếu dữ liệu' });
    }

    // 1. Lấy doctor_id và patient_id từ Appointments
    const getDoctorPatientQuery = 'SELECT doctor_id, patient_id FROM Appointments WHERE appointment_id = ?';

    connection.query(getDoctorPatientQuery, [appointment_id], (err, appointmentResults) => {
        if (err) {
            console.error('Lỗi truy vấn Appointments:', err);
            return res.status(500).json({ message: 'Lỗi truy vấn lịch hẹn' });
        }

        if (appointmentResults.length === 0) {
            return res.status(404).json({ message: 'Không tìm thấy lịch hẹn' });
        }

        const { doctor_id, patient_id } = appointmentResults[0];

        // 2. Thêm vào bảng Prescriptions
        const insertPrescriptionQuery = `
            INSERT INTO Prescriptions (appointment_id, doctor_id, patient_id)
            VALUES (?, ?, ?)
        `;

        connection.query(insertPrescriptionQuery, [appointment_id, doctor_id, patient_id], (err, prescriptionResult) => {
            if (err) {
                console.error('Lỗi khi thêm đơn thuốc:', err);
                return res.status(500).json({ message: 'Lỗi khi thêm đơn thuốc' });
            }

            const prescription_id = prescriptionResult.insertId;

            // 3. Thêm vào bảng Prescription_Details
            const insertDetailQuery = `
                INSERT INTO Prescription_Details (prescription_id, medicine_name, dosage, usage_instruction, duration)
                VALUES (?, ?, ?, ?, ?)
            `;

            connection.query(insertDetailQuery, [prescription_id, medicine_name, dosage, usage_instruction, duration], (err) => {
                if (err) {
                    console.error('Lỗi khi thêm chi tiết đơn thuốc:', err);
                    return res.status(500).json({ message: 'Lỗi khi thêm chi tiết đơn thuốc' });
                }

                // 4. Thành công
                return res.status(201).json({
                    message: 'Success',
                });
            });
        });
    });
};

const update_preliminary = (req, res) => {
    const { appointment_id, preliminary_diagnosis, final_conclusion, recommendations } = req.body;

    // Lấy doctor_id từ bảng Appointments
    connection.query('SELECT doctor_id FROM Appointments WHERE appointment_id = ?', [appointment_id], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Lỗi khi truy vấn doctor_id' });
        }

        if (results.length === 0) {
            return res.status(404).json({ error: 'Không tìm thấy lịch hẹn tương ứng' });
        }

        const doctor_id = results[0].doctor_id;

        // Kiểm tra tồn tại Preliminary_Diagnoses
        connection.query('SELECT diagnosis_id FROM Preliminary_Diagnoses WHERE appointment_id = ?', [appointment_id], (err, diagResults) => {
            if (err) {
                return res.status(500).json({ error: 'Lỗi khi kiểm tra Preliminary_Diagnoses' });
            }

            const queryPreliminary = diagResults.length > 0
                ? `UPDATE Preliminary_Diagnoses SET preliminary_diagnosis = ?, doctor_id = ? WHERE appointment_id = ?`
                : `INSERT INTO Preliminary_Diagnoses (appointment_id, doctor_id, preliminary_diagnosis) VALUES (?, ?, ?)`;

            const preliminaryValues = diagResults.length > 0
                ? [preliminary_diagnosis, doctor_id, appointment_id]
                : [appointment_id, doctor_id, preliminary_diagnosis];

            // Thực hiện query cho Preliminary_Diagnoses
            connection.query(queryPreliminary, preliminaryValues, (err) => {
                if (err) {
                    return res.status(500).json({ error: 'Lỗi khi xử lý Preliminary_Diagnoses' });
                }

                // Kiểm tra tồn tại Final_Conclusions
                connection.query('SELECT conclusion_id FROM Final_Conclusions WHERE appointment_id = ?', [appointment_id], (err, conclResults) => {
                    if (err) {
                        return res.status(500).json({ error: 'Lỗi khi kiểm tra Final_Conclusions' });
                    }

                    const queryConclusions = conclResults.length > 0
                        ? `UPDATE Final_Conclusions SET final_conclusion = ?, recommendations = ?, doctor_id = ? WHERE appointment_id = ?`
                        : `INSERT INTO Final_Conclusions (appointment_id, doctor_id, final_conclusion, recommendations) VALUES (?, ?, ?, ?)`;

                    const conclusionValues = conclResults.length > 0
                        ? [final_conclusion, recommendations, doctor_id, appointment_id]
                        : [appointment_id, doctor_id, final_conclusion, recommendations];

                    // Thực hiện query cho Final_Conclusions
                    connection.query(queryConclusions, conclusionValues, (err) => {
                        if (err) {
                            return res.status(500).json({ error: 'Lỗi khi xử lý Final_Conclusions' });
                        }

                        res.status(200).json({ message: 'Success' });
                    });
                });
            });
        });
    });
};

const update_prescriptions = (req, res) => {
    const { prescription_id, medicine_name, dosage, usage_instruction, duration } = req.body;

    if (!prescription_id || !medicine_name || !dosage || !usage_instruction || !duration) {
        return res.status(400).json({ error: 'Tất cả các trường là bắt buộc' });
    }

    const query = `
        UPDATE Prescription_Details
        SET medicine_name = ?, dosage = ?, usage_instruction = ?, duration = ?
        WHERE prescription_id = ?
    `;

    connection.query(query, [medicine_name, dosage, usage_instruction, duration, prescription_id], (err) => {
        if (err) {
            console.error('Lỗi khi cập nhật đơn thuốc:', err);
            return res.status(500).json({ error: 'Lỗi khi cập nhật đơn thuốc' });
        }

        res.status(200).json({ message: 'Success' });
    });
};

const delete_prescriptions = (req, res) => {
    const { prescription_id } = req.body;

    if (!prescription_id) {
        return res.status(400).json({ error: 'prescription_id là bắt buộc' });
    }

    // Xóa chi tiết đơn thuốc trong Prescription_Details
    const deleteDetailsQuery = `
        DELETE FROM Prescription_Details
        WHERE prescription_id = ?
    `;

    // Xóa đơn thuốc trong Prescriptions
    const deletePrescriptionQuery = `
        DELETE FROM Prescriptions
        WHERE prescription_id = ?
    `;

    // Xóa chi tiết đơn thuốc và đơn thuốc trong Prescriptions
    connection.query(deleteDetailsQuery, [prescription_id], (err) => {
        if (err) {
            console.error('Lỗi khi xóa chi tiết đơn thuốc:', err);
            return res.status(500).json({ error: 'Lỗi khi xóa chi tiết đơn thuốc' });
        }

        // Sau khi xóa chi tiết đơn thuốc, xóa đơn thuốc
        connection.query(deletePrescriptionQuery, [prescription_id], (err) => {
            if (err) {
                console.error('Lỗi khi xóa đơn thuốc:', err);
                return res.status(500).json({ error: 'Lỗi khi xóa đơn thuốc' });
            }

            res.status(200).json({ message: 'Success' });
        });
    });
};



module.exports = {
    appointment_detail,
    preliminary_and_prescriptions,
    add_prescriptions,
    update_preliminary,
    update_prescriptions,
    delete_prescriptions
}