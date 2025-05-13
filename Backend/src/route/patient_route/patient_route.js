const patientHomeController = require('../../controller/patient_controller/patient_home_controller')
const patientSpecialistController = require('../../controller/patient_controller/patient_specialist_controller')
const patientAppointmentController = require('../../controller/patient_controller/patient_appointment_controller')
const patientCommentController = require('../../controller/patient_controller/patient_comment_controller')
const verify = require('../../middleware/token_action')
const express = require('express');

const router = express.Router();

router.get('/patient-home', verify.verifyToken, patientHomeController.patient_info);
router.get('/specialists', verify.verifyToken, patientHomeController.specialists);
router.get('/top-doctors', verify.verifyToken, patientHomeController.top_doctors);
router.get('/search-result', verify.verifyToken, patientHomeController.search_result)
router.get('/all-specialists', verify.verifyToken, patientSpecialistController.get_all_specialists)
router.get('/get-all-doctors-in-specialists', verify.verifyToken, patientSpecialistController.get_all_doctors_in_specialists)
router.post('/doctor-detail', verify.verifyToken, patientAppointmentController.doctor_detail)
router.post('/get-doctor-in-specialist', verify.verifyToken, patientSpecialistController.get_all_doctors_in_specialists)
router.post('/get-specialist', verify.verifyToken, patientSpecialistController.get_specialist)
router.post('/all-appointment', verify.verifyToken, patientAppointmentController.all_appointments)
router.post('/get-all-comments', verify.verifyToken, patientCommentController.get_all_comments)
router.post('/get-all-reply', verify.verifyToken, patientCommentController.get_all_reply)
router.post('/add-comment', verify.verifyToken, patientCommentController.add_comment)
router.post('/add-reply', verify.verifyToken, patientCommentController.add_reply)
router.post('/create-appointment', verify.verifyToken, patientAppointmentController.create_appointment)
router.post('/check-doctor-appointment', verify.verifyToken, patientAppointmentController.check_doctor_appointment)
router.post('/get-appointment-status', verify.verifyToken, patientHomeController.get_appointment_status)
router.post('/get-appointment-detail', verify.verifyToken, patientAppointmentController.get_appointment_detail)

module.exports = router;