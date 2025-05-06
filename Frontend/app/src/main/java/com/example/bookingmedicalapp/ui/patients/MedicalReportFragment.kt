package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentMedicalReportBinding

internal class MedicalReportFragment : BaseDataBindingFragment<FragmentMedicalReportBinding, MedicalReportViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            MedicalReportFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_medical_report

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        TODO("Not yet implemented")
    }
}