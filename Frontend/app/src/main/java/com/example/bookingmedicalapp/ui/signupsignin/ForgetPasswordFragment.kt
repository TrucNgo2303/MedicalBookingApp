package com.example.bookingmedicalapp.ui.signupsignin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentForgetPasswordBinding
import com.example.bookingmedicalapp.model.SendCodeRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addFragment
import io.reactivex.disposables.CompositeDisposable

internal class ForgetPasswordFragment : BaseDataBindingFragment<FragmentForgetPasswordBinding, ForgetPasswordViewModel>(){

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private var email = ""

    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            ForgetPasswordFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_forget_password

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {

    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mBinding.btnSendCode.setOnClickListener {
            callApi()
        }
    }

    private fun callApi(){
        email = mBinding.edtEmail.text.toString().trim()

        compositeDisposable.add(
            repository.sendCode(SendCodeRequest(email, "ForgetPassword")).subscribe({response ->

                Log.d("Api","Api Response: $response")
                mainViewModel.email = email
                mainViewModel.type = response.type


                if (response.message == "Success send code" && response.type == "ForgetPassword") {
                    parentFragmentManager.addFragment(fragment = VerifyCodeFragment.newInstance())
                }else{
                    mBinding.tvError.visibility = View.VISIBLE
                }

            },{throwable ->
                Log.d("Api", "API Error ${throwable.message}")
            })
        )
    }
}