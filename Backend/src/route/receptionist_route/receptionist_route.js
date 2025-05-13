const receptionistController = require('../../controller/receptionist_controller/receptionist_controller');

const express = require('express');

const router = express.Router();

router.get('/get-online-appointments', receptionistController.get_online_appointments);
router.post('/get-online-appointments-detail', receptionistController.get_online_appointments_detail);
router.post('/payment-status-update', receptionistController.payment_status_update);
router.post('/add-to-waiting-list', receptionistController.add_to_waiting_list);
router.post('/check-phone-number', receptionistController.check_phone_number);
router.post('/add-customer-offline', receptionistController.add_customer_offline);
router.get('/all-specialist', receptionistController.all_specialist);
router.post('/check-doctor', receptionistController.check_doctor);
router.post('/add-offline-patient', receptionistController.add_offline_patient);

module.exports = router;