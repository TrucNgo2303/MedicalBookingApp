package com.example.bookingmedicalapp.ui.receptionist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.DoctorAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentOnlineAppointmentBinding
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable

internal class OnlineAppointmentFragment : BaseDataBindingFragment<FragmentOnlineAppointmentBinding, OnlineAppointmentViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var appointmentAdapter: DoctorAppointmentAdapter

    companion object {
        @JvmStatic
        fun newInstance() =
            OnlineAppointmentFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_online_appointment

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            onBackFragmentPressed()
        }
    }

    override fun initialize() {
        onLeftIconClick()
    }

    private fun callApi(){

    }


}