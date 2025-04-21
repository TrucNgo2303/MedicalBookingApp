package com.example.bookingmedicalapp.ui.bottomNav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.ui.patients.DoctorDetailFragment
import com.example.bookingmedicalapp.ui.patients.PatientAppointmentFragment
import com.example.bookingmedicalapp.ui.patients.PatientHomeFragment
import com.example.bookingmedicalapp.utils.addWithNavFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav_main)
        // Mặc định load HomeFragment khi mở app
        if (savedInstanceState == null) {
            loadFragment(PatientHomeFragment())
        }
        supportFragmentManager.addWithNavFragment(R.id.fragment_container,fragment = PatientHomeFragment.newInstance())
        // Xử lý khi click vào các item của BottomNavigationView
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> loadFragment(PatientHomeFragment())
                    R.id.nav_appointment -> loadFragment(PatientAppointmentFragment())
                    R.id.nav_favourite -> loadFragment(DoctorDetailFragment())
                    R.id.nav_profile -> loadFragment(ProfileFragment())
                }
                true
            }
    }

    // Hàm chuyển Fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}