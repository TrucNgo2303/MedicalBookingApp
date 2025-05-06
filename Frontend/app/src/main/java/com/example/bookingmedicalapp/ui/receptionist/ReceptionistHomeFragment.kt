package com.example.bookingmedicalapp.ui.receptionist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentReceptionistHomeBinding
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment

internal class ReceptionistHomeFragment : BaseDataBindingFragment<FragmentReceptionistHomeBinding, ReceptionistHomeViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            ReceptionistHomeFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_receptionist_home

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        mBinding.btnAddWaiting.setOnClickListener {
            parentFragmentManager.addFragment(fragment = OfflineAppointmentFragment.newInstance())
        }
        mBinding.btnLogout.setOnClickListener {
            val sharedPref = TokenAction(requireContext())
            sharedPref.clearToken()
            parentFragmentManager.addFragment(fragment = SignInFragment.newInstance())
        }
    }
}