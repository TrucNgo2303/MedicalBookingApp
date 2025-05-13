package com.example.bookingmedicalapp.ui.bottomNav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.ui.doctors.DoctorHomeFragment
import com.example.bookingmedicalapp.ui.doctors.DoctorScheduleFragment
import com.example.bookingmedicalapp.ui.patients.DoctorDetailFragment
import com.example.bookingmedicalapp.ui.patients.PatientAppointmentFragment
import com.example.bookingmedicalapp.ui.patients.PatientHomeFragment
import com.example.bookingmedicalapp.utils.addWithNavDocTorFragment
import com.example.bookingmedicalapp.utils.addWithNavFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavDoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav_doctor)
        // Mặc định load HomeFragment khi mở app
        if (savedInstanceState == null) {
            loadFragment(DoctorHomeFragment())
        }
        supportFragmentManager.addWithNavDocTorFragment(R.id.fragment_container_doctor,fragment = DoctorHomeFragment.newInstance())
        // Xử lý khi click vào các item của BottomNavigationView
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> loadFragment(DoctorHomeFragment())
                    R.id.nav_appointment -> loadFragment(DoctorScheduleFragment())
                    R.id.nav_profile -> {
                        Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_doctor, fragment)
            .addToBackStack(null)
            .commit()
    }
}