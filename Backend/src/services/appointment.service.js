const connection = require('../config/database');
const moment = require('moment-timezone');

const cancelOverdueAppointments = () => {
    const nowVN = moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD HH:mm:ss');

    const query = `
        UPDATE Appointments
        SET status = 'Cancelled'
        WHERE status IN ('Pending', 'Confirmed')
          AND TIMESTAMPDIFF(MINUTE, appointment_datetime, ?) > 15
    `;

    // Dùng connection.query với callback
    connection.query(query, [nowVN], (err, results) => {
        if (err) {
            console.error('Lỗi khi cập nhật trạng thái lịch hẹn:', err);
            return;
        }

        // Kết quả trả về sau khi update
        console.log(`Đã hủy ${results.affectedRows} lịch hẹn quá hạn (tính theo giờ VN).`);
    });
};


const checkAppointmentsStatus = () => {
    const nowVN = moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD HH:mm:ss');

    // 1. Cập nhật những cuộc hẹn quá 2 tiếng và chưa thanh toán, chưa cọc → Cancelled
    const cancelQuery = `
        UPDATE Appointments
        SET status = 'Cancelled'
        WHERE status = 'Pending'
          AND is_paid = FALSE
          AND is_deposit = FALSE
          AND TIMESTAMPDIFF(MINUTE, created_at, ?) >= 120
    ;`

    // 2. Cập nhật những cuộc hẹn đã thanh toán hoặc đã đặt cọc → Confirmed
    const confirmQuery = `
        UPDATE Appointments
        SET status = 'Confirmed'
        WHERE status = 'Pending'
          AND (is_paid = TRUE OR is_deposit = TRUE)
    ;`

    connection.query(cancelQuery, [nowVN], (err, cancelResult) => {
        if (err) {
            console.error('Lỗi khi cập nhật trạng thái Cancelled:', err);
            return;
        }
        console.log(`Đã hủy ${cancelResult.affectedRows} lịch hẹn quá hạn.`);
    });

    connection.query(confirmQuery, (err, confirmResult) => {
        if (err) {
            console.error('Lỗi khi cập nhật trạng thái Confirmed:', err);
            return;
        }
        console.log(`Đã xác nhận ${confirmResult.affectedRows} lịch hẹn.`);
    });
};


module.exports = {
    cancelOverdueAppointments,
    checkAppointmentsStatus
};

