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

module.exports = { cancelOverdueAppointments };

