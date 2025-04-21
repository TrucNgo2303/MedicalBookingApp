package com.example.bookingmedicalapp.model

data class LoginBody(
    val message: String?,
    val token: String,
    val authorization_id: Int?
)

data class LoginRequest(
    val email: String?,
    val password: String?
)

data class SendCodeRequest(
    val email: String?,
    val type: String?
)

data class SendCodeBody(
    val message: String?,
    val type: String?
)

data class VerifyCodeRequest(
    val email: String?,
    val code: String?,
    val type: String?
)

data class VerifyCodeBody(
    val message: String?,
    val type : String?
)

data class SignUpRequest(
    val full_name: String?,
    val email: String?,
    val password: String?
)

data class SignUpBody(
    val message: String?,
    val type: String?,
)

data class CreateProfileRequest(
    val email: String?,
    val password: String?,
    val full_name: String?,
    val phone_number: String?,
    val date_of_birth: String?,
    val gender: String?,
    val address: String?,
    val avatar: String?,
)

data class CreateProfileBody(
    val message: String?,
    val authorization_id: Int?
)