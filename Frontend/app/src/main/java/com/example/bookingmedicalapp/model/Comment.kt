package com.example.bookingmedicalapp.model

data class CommentRequest(
    val doctor_id: Int?
)
data class ReplyRequest(
    val comment_id: Int?
)
data class CommentResponse(
    val comment_id: Int,
    val comment_detail: String,
    val star: Int,
    val created_at: String,
    val patient_name: String,
    val patient_avatar: String
)

data class ReplyResponse(
    val reply_id: Int,
    val reply_detail: String,
    val user_type: String,
    val user_name: String,
    val user_avatar: String
)

data class Reply(
    val name: String,
    val role: String,
    val time: String,
    val description: String
)

data class Comment(
    val name: String,
    val role: String,
    val time: String,
    val rating: Float,
    val description: String,
    val replies: List<Reply>
)