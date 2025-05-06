package com.example.bookingmedicalapp.model

data class AppointmentNotiItem(
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