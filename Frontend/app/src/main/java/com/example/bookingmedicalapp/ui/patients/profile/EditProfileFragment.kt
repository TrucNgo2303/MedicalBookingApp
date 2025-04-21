package com.example.bookingmedicalapp.ui.patients.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentEditProfileBinding


internal class EditProfileFragment : BaseDataBindingFragment<FragmentEditProfileBinding, EditProfileViewModel>() {
    companion object {

        @JvmStatic
        fun newInstance() =
            EditProfileFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_edit_profile

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        TODO("Not yet implemented")
    }
}