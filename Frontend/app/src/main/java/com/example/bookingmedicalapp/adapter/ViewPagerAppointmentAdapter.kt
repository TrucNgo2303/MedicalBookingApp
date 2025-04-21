package com.example.bookingmedicalapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bookingmedicalapp.ui.patients.filter.CancelledFragment
import com.example.bookingmedicalapp.ui.patients.filter.CompletedFragment
import com.example.bookingmedicalapp.ui.patients.filter.PendingFragment
import com.example.bookingmedicalapp.ui.patients.filter.UpcomingFragment

class ViewPagerAppointmentAdapter(fragment: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return  4;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingFragment()
            1 -> UpcomingFragment()
            2 -> CompletedFragment()
            3 -> CancelledFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

}
