package com.example.bookingmedicalapp.model

data class BaseDoctorResponse<T>(
    val message: String,
    val data: T
)

data class NumOfAppointment(
    val number_of_appointment_today: Int,
    val doctor_id: Int
)

data class TodayAppointmentResponse(
    val appointment_id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val appointment_datetime: String,
    val status: String,
    val avatar: String,
    val full_name: String,
    val is_online: Int
)

data class DoctorAppointment(
    val appointment_id: Int,
    val avatar: String,
    val patient_name: String,
    val status: String,
    val date: String,
    val time: String,
    val is_online: Int
)

data class AppointmentDetailRequest(
    val appointment_id: Int?
)

data class  AppointmentDetailResponse(
    val patient_id: Int,
    val patient_avatar: String,
    val patient_name: String,
    val doctor_id: Int,
    val doctor_name: String,
    val specialty: String,
    val reason: String,
    val status: String,
    val is_online: Int,
    val consultation_fee: String,
    val has_preliminary_diagnosis: Int,
    val has_prescription: Int,
    val appointment_date: String,
    val appointment_time: String,
)

data class PreliminaryDetailResponse(
    val appointment_id: Int,
    val preliminary_diagnosis: String,
    val final_conclusion: String,
    val recommendations: String,
    val prescriptionDetails: List<PrescriptionDetails>
)
data class PrescriptionDetails(
    val detail_id: Int,
    val prescription_id: Int,
    val medicine_name: String,
    val dosage: String,
    val usage_instruction: String,
    val duration: Int,
)

data class AddPrescriptionsRequest(
    val appointment_id: Int?,
    val medicine_name: String,
    val dosage: String,
    val usage_instruction: String,
    val duration: Int
)

data class NormalResponse(
    val message: String
)

data class UpdatePreliminaryRequest(
    val appointment_id: Int?,
    val preliminary_diagnosis: String,
    val final_conclusion: String,
    val recommendations: String,
)

data class UpdatePrescriptionsRequest(
    val prescription_id: Int,
    val medicine_name: String,
    val dosage: String,
    val usage_instruction: String,
    val duration: Int
)

data class PrescriptionsRequest(
    val prescription_id: Int,
)

data class AppointmentDateRequest(
    val appointment_date: String,
)