package com.example.bookingmedicalapp.model

data class AppointmentNotiItem(
    val date: String,
    val time: String,
    val avatar: Int,
    val name: String,
    val specialist: String,
    val fee: String,
)