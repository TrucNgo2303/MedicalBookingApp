const doctorHomeController = require('../../controller/doctor_controller/doctor_home_controller')
const doctorAppointmentController = require('../../controller/doctor_controller/doctor_appointment_controller')
const doctorScheduleController = require('../../controller/doctor_controller/doctor_schedule_controller')

const express = require('express');
const verify = require('../../middleware/token_action')

const router = express.Router();

router.get('/number-of-appointment-today', verify.verifyToken, doctorHomeController.number_of_appointment_today)
router.get('/today-appointment', verify.verifyToken, doctorHomeController.today_appointment)
router.post('/appointment-detail', verify.verifyToken, doctorAppointmentController.appointment_detail)
router.post('/preliminary-and-prescriptions', verify.verifyToken, doctorAppointmentController.preliminary_and_prescriptions)
router.post('/add-prescriptions', verify.verifyToken, doctorAppointmentController.add_prescriptions)
router.post('/update-prescriptions', verify.verifyToken, doctorAppointmentController.update_prescriptions)
router.post('/update-preliminary', verify.verifyToken, doctorAppointmentController.update_preliminary)
router.delete('/delete-prescriptions', verify.verifyToken, doctorAppointmentController.delete_prescriptions)
router.post('/doctor-schedule', verify.verifyToken, doctorScheduleController.doctor_schedule)

module.exports = router;