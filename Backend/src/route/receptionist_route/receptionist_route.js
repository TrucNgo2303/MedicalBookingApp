const receptionistController = require('../../controller/receptionist_controller/receptionist_controller');

const express = require('express');
const verify = require('../../middleware/token_action')

const router = express.Router();

router.get('/get-all-appointment-today', verify.verifyToken, receptionistController.get_all_appointment_today)
router.post('/check-doctor-schedule', verify.verifyToken, receptionistController.check_doctor_schedule)
router.post('/add-waiting', verify.verifyToken, receptionistController.add_waiting)

module.exports = router;