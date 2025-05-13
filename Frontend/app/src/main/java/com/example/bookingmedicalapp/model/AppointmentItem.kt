package com.example.bookingmedicalapp.model

data class AppointmentNotiItem(
    val appointmentId: Int,
    val date: String,
    val time: String,
    val avatar: String?,
    val name: String,
    val specialist: String,
    val fee: String,
)

data class AppointmentRequest(
    val patient_id: Int?,
    val doctor_id: Int?,
    val appointment_date: String?,
    val appointment_time: String?,
    val status: String?,
    val reason: String?,
    val is_deposit: Boolean?,
    val consultation_fee: Double?,
    val is_online: Boolean?
)

data class AppointmentResponse(
    val message: String
)

data class CheckAppointmentResponse(
    val appointment_datetime: String
)

data class AppointmentIdRequest(
    val appointment_id: Int?
)

data class AppointmentDetailPatientResponse(
    val appointment_id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val doctor_name: String,
    val doctor_avatar: String,
    val doctor_specialist: String,
    val patient_name: String,
    val appointment_date: String,
    val appointment_time: String,
    val reason: String,
    val status: String,
    val is_online: Int,
    val consultation_fee: String,
    val final_conclusion: String,
    val recommendations: String,
    val has_prescription: Int
)