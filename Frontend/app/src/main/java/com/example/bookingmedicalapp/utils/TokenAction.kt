package com.example.bookingmedicalapp.utils

import android.content.Context
import android.content.SharedPreferences

class TokenAction(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveToken(token: String) {
        editor.putString("TOKEN", token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("TOKEN", null)
    }

    fun clearToken(): Any? {
        editor.remove("TOKEN")
        editor.apply()
        return null
    }
}