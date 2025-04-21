package com.example.bookingmedicalapp.ui.signupsignin

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.common.Constants
import com.example.bookingmedicalapp.databinding.FragmentSignInBinding
import com.example.bookingmedicalapp.model.LoginRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.ui.bottomNav.BottomNavMainActivity
import com.example.bookingmedicalapp.ui.patients.PatientHomeFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment
import io.reactivex.disposables.CompositeDisposable

internal class SignInFragment : BaseDataBindingFragment<FragmentSignInBinding,SignInViewModel>() {

    private var isPasswordVisible = false
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    companion object {
        @JvmStatic
        fun newInstance() =
            SignInFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int  = R.layout.fragment_sign_in

    override fun onBackFragmentPressed() {
    }

    override fun onLeftIconClick() {

    }

    override fun initialize() {
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
        mBinding.btnSignIn.setOnClickListener {
            callApi()
        }

        mBinding.tvForget.setOnClickListener {
            parentFragmentManager.addFragment(fragment = ForgetPasswordFragment.newInstance())
        }

        mBinding.tvSignUp.setOnClickListener {
            parentFragmentManager.addFragment(fragment = SignUpFragment.newInstance())
        }
        val sharedPref = TokenAction(requireContext())
        val token = sharedPref.getToken()
        Log.d("API","Token hiện tại: $token")

    }

    private fun callApi(){
        val email = mBinding.edtEmail.text.toString().trim()
        val password = mBinding.edtPassword.text.toString().trim()

        compositeDisposable.add(
            repository.login(LoginRequest(email, password)).subscribe({ response ->

                val sharedPref = TokenAction(requireContext())
                sharedPref.saveToken(response.token)
                Constants.token = sharedPref.getToken()
                val intent =  Intent(requireContext(), BottomNavMainActivity::class.java)
                startActivity(intent)
                Log.d("Api", "Api Response $response")

            }, { throwable ->
                Log.d("Api", "API Error ${throwable.message}")

            })
        )
    }
}