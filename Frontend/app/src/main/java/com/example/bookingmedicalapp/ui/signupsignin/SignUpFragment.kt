package com.example.bookingmedicalapp.ui.signupsignin

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentSignUpBinding
import com.example.bookingmedicalapp.model.LoginRequest
import com.example.bookingmedicalapp.model.SignUpRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addFragment
import io.reactivex.disposables.CompositeDisposable

internal class SignUpFragment : BaseDataBindingFragment<FragmentSignUpBinding, SignUpViewModel>() {

    private var isPasswordVisible = false
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_sign_up

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mBinding.imvViewPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                // Hiển thị mật khẩu
                mBinding.edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                mBinding.imvViewPassword.setImageResource(R.drawable.eye_open) // Cập nhật icon mắt mở
            } else {
                // Ẩn mật khẩu
                mBinding.edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                mBinding.imvViewPassword.setImageResource(R.drawable.eye_close) // Cập nhật icon mắt đóng
            }

            // Đưa con trỏ về cuối văn bản
            mBinding.edtPassword.text?.let { it1 -> mBinding.edtPassword.setSelection(it1.length) }
        }


        mBinding.btnSignUp.setOnClickListener {
            callApi()
        }

    }

    private fun callApi(){
        val fullName = mBinding.edtName.text.toString().trim()
        val email = mBinding.edtEmail.text.toString().trim()
        val password = mBinding.edtPassword.text.toString().trim()

        compositeDisposable.add(
            repository.signUp(SignUpRequest(fullName, email, password)).subscribe({ response ->
                if(response.message == "Success send code") {
                    mainViewModel.email = email
                    mainViewModel.full_name = fullName
                    mainViewModel.password = password
                    mainViewModel.type = response.type
                    parentFragmentManager.addFragment(fragment = VerifyCodeFragment.newInstance())
                }

                Log.d("Api", "Api Response $response")

            }, { throwable ->
                Log.d("Api", "API Error ${throwable.message}")

            })
        )
    }
}