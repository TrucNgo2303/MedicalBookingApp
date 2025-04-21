package com.example.bookingmedicalapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.ui.bottomNav.BottomNavMainActivity
import com.example.bookingmedicalapp.ui.patients.PatientHomeFragment
import com.example.bookingmedicalapp.ui.patients.SearchFragment
import com.example.bookingmedicalapp.ui.signupsignin.CreateProfileFragment
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.ui.signupsignin.SignUpFragment
import com.example.bookingmedicalapp.ui.signupsignin.VerifyCodeFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val sharedPref = TokenAction(this)
        val token = sharedPref.getToken()

        if (token != null) {
            // Token hợp lệ, không cần đăng nhập lại
            Log.d("API","Token hiện tại: $token")
            val intent =  Intent(this, BottomNavMainActivity::class.java)
            startActivity(intent)

        } else {
            // Không có token, chuyển về màn hình đăng nhập
            Log.d("API","Người dùng chưa đăng nhập")
            supportFragmentManager.addFragment(fragment = SignInFragment.newInstance())
        }
    }
}