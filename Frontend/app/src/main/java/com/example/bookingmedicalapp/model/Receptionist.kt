package com.example.bookingmedicalapp.model

data class CheckDoctorRequest(
    val specialist_name: String,
    val appointment_time: String
)

data class CheckDoctorResponse(
    val doctor_id: Int,
    val full_name: String
)

data class AddWaitingRequest(
    val patient_name: String,
    val phone_number: String,
    val address: String,
    val date_of_birth: String?,
    val doctor_id: Int?,
    val request_time: String?
)