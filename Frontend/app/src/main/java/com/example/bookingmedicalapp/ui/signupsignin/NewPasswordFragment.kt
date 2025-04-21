package com.example.bookingmedicalapp.ui.signupsignin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentNewPasswordBinding
import com.example.bookingmedicalapp.utils.addFragment

internal class NewPasswordFragment : BaseDataBindingFragment<FragmentNewPasswordBinding, NewPasswordViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance() =
            NewPasswordFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_new_password

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        mBinding.btnChangePasword.setOnClickListener {
            parentFragmentManager.addFragment(fragment = SignInFragment.newInstance())
        }
    }
}