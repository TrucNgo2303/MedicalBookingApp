package com.example.bookingmedicalapp.application

import android.app.Application
import com.example.bookingmedicalapp.common.Constants
import com.example.bookingmedicalapp.utils.TokenAction

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val shared = TokenAction(this)
        Constants.token = shared.getToken()
    }
}