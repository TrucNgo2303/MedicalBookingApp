const cron = require('node-cron');
const { cancelOverdueAppointments } = require('../services/appointment.service');

// Chạy mỗi phút
cron.schedule('* * * * *', async () => {
    console.log('Kiểm tra lịch hẹn quá hạn...');
    await cancelOverdueAppointments();
});
