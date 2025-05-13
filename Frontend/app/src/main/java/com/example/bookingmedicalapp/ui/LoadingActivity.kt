package com.example.bookingmedicalapp.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.ui.bottomNav.BottomNavDoctorActivity
import com.example.bookingmedicalapp.ui.bottomNav.BottomNavMainActivity
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment
import org.json.JSONObject

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val sharedPref = TokenAction(this)
        val token = sharedPref.getToken()

        if (token != null) {
            // Token hợp lệ, decode để lấy role
            Log.d("API", "Token hiện tại: $token")
            try {
                val role = decodeJwtRole(token)
                Log.d("API", "Role: $role")
                when (role) {
                    "Patient" -> {
                        val intent = Intent(this, BottomNavMainActivity::class.java)
                        startActivity(intent)
                        finish() // Kết thúc Activity hiện tại để ngăn quay lại
                    }
                    "Doctor" -> {
                        // Điều hướng đến DoctorHomeFragment
                        val intent = Intent(this, BottomNavDoctorActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        // Xử lý trường hợp role không xác định
                        Log.d("API", "Unknown role: $role")
                        // Chuyển về màn hình đăng nhập
                        supportFragmentManager.addFragment(fragment = SignInFragment.newInstance())
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Error decoding token: ${e.message}")
                supportFragmentManager.addFragment(fragment = SignInFragment.newInstance())
            }
        } else {
            // Không có token, chuyển về màn hình đăng nhập
            Log.d("API", "Người dùng chưa đăng nhập")
            supportFragmentManager.addFragment(fragment = SignInFragment.newInstance())
        }
    }

    private fun decodeJwtRole(token: String): String {
        try {
            // JWT có dạng [header].[payload].[signature]
            val parts = token.split(".")
            if (parts.size != 3) {
                throw IllegalArgumentException("Invalid JWT format")
            }
            // Decode payload (phần thứ 2)
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charsets.UTF_8)
            // Parse payload thành JSON
            val json = JSONObject(payload)
            // Lấy role từ JSON
            return json.getString("role") // Giả sử role nằm trong key "role"
        } catch (e: Exception) {
            throw Exception("Failed to decode JWT: ${e.message}")
        }
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}