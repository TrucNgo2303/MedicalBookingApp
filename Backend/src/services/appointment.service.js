const connection = require('../../config/database');
const moment = require('moment-timezone');

const cancelOverdueAppointments = async () => {
    try {
        // Lấy thời gian hiện tại theo múi giờ Việt Nam
        const nowVN = moment().tz('Asia/Ho_Chi_Minh').format('YYYY-MM-DD HH:mm:ss');

        // Truy vấn và cập nhật các lịch hẹn đã quá 15 phút
        const [results] = await connection.execute(`
      UPDATE Appointments
      SET status = 'Cancelled'
      WHERE status IN ('Pending', 'Confirmed')
        AND TIMESTAMPDIFF(MINUTE, appointment_datetime, ?) > 15
    `, [nowVN]);

        console.log(`Đã hủy ${results.affectedRows} lịch hẹn quá hạn (tính theo giờ VN).`);
    } catch (error) {
        console.error('Lỗi khi cập nhật trạng thái lịch hẹn:', error);
    }
};

module.exports = { cancelOverdueAppointments };

