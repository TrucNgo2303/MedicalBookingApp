package com.example.bookingmedicalapp.model

data class BasePatientResponse<T>(
    val message: String?,
    val data: T
)

data class PatientHomeResponse(
    val full_name: String?,
    val patient_id: Int?,
    val avatar: String?
)
data class SpecialistHomeResponse(
    val specialist_id: Int?,
    val specialist_name: String?,
    val icon: String?
)

data class TopDoctorResponse(
    val doctor_id: Int?,
    val full_name: String?,
    val specialty: String?,
    val qualification: String?,
    val average_star: String?,
    val avatar: String?
)

data class SearchResultBody(
    val doctor_id: Int?,
    val specialist_id: Int?,
    val name: String
)

data class DoctorDetailRequest(
    val doctor_id: Int?
)

data class DoctorDetailResponse(
    val doctor_id: Int?,
    val full_name: String?,
    val phone_number: String?,
    val specialty: String?,
    val qualification: String?,
    val experience_years: Int?,
    val summary: String?,
    val avatar: String?,
    val patient_count: String?,
    val consultation_fee: String?,
    val average_star: String?,
    val total_comments: String?,
)

data class DateModel(
    val day: String,
    val date: Int,
    val fullDate: String,
    val month: Int
    )


data class AllSpecialistResponse(
    val specialist_id: Int?,
    val specialist_name: String?,
    val summary: String?,
    val icon: String?,
    val consultation_fee:String?,
    val numberOfDoctor: Int?,
)

data class SpecialistDetailResponse(
    val doctor_id: Int?,
    val full_name: String?,
    val specialty: String?,
    val qualification: String?,
    val avatar: String?,
    val average_star: String?
)
data class SpecialistDetailRequest(
    val specialist_id: Int?
)

data class SpecialistResponse(
    val specialist_id: Int?,
    val specialist_name: String?,
    val summary: String?,
    val icon: String?,
    val consultation_fee: String?
)

data class StatusRequest(
    val status: String
)

data class StatusResponse(
    val appointment_id: Int,
    val doctor_id: Int,
    val appointment_datetime: String,
    val doctor_avatar: String,
    val doctor_name: String,
    val specialist: String,
    val consultation_fee: String,
)

