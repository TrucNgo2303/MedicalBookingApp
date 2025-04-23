const doctorHomeController = require('../../controller/doctor_controller/doctor_home_controller')
const doctorAppointmentController = require('../../controller/doctor_controller/doctor_appointment_controller')

const express = require('express');
const verify = require('../../middleware/token_action')

const router = express.Router();

router.get('/number-of-appointment-today', verify.verifyToken, doctorHomeController.number_of_appointment_today)
router.get('/today-appointment', verify.verifyToken, doctorHomeController.today_appointment)
router.post('/appointment-detail', verify.verifyToken, doctorAppointmentController.appointment_detail)

module.exports = router;