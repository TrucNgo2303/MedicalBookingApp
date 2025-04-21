package com.example.bookingmedicalapp.ui.doctors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentDoctorHomeBinding

internal class DoctorHomeFragment : BaseDataBindingFragment<FragmentDoctorHomeBinding, DoctorHomeViewModel>() {
    companion object {
        @JvmStatic
        fun newInstance() =
            DoctorHomeFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_doctor_home

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
    }

    override fun initialize() {

    }
}