package com.example.bookingmedicalapp.ui.signupsignin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentVerifyCodeBinding
import com.example.bookingmedicalapp.model.VerifyCodeRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addFragment
import io.reactivex.disposables.CompositeDisposable

internal class VerifyCodeFragment : BaseDataBindingFragment<FragmentVerifyCodeBinding, VerifyCodeViewModel>() {

    private lateinit var codeInputs: List<AppCompatEditText>
    private lateinit var mainViewModel: MainViewModel
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private var email = ""
    private var type = ""
    var code = ""

    companion object {
        @JvmStatic
        fun newInstance() =
            VerifyCodeFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_verify_code

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        codeInputs = listOf(
            mBinding.code1,
            mBinding.code2,
            mBinding.code3,
            mBinding.code4,
            mBinding.code5,
            mBinding.code6,
            )
        setupCodeInputListeners()



        mBinding.btnVerify.setOnClickListener {
            code = codeInputs.joinToString("") { it.text.toString() }

            callApi()
        }

    }

    private fun callApi(){
        email = mainViewModel.email.toString()
        type = mainViewModel.type.toString()
        Log.d("Api","Email: $email")
        Log.d("Api","Code: $code")
        Log.d("Api","Type: $type")

        compositeDisposable.add(
            repository.verifyCode(VerifyCodeRequest(email, code, type)).subscribe({ response->
                Log.d("Api","Api Response: $response")
                if (response.message == "Success" && response.type == "ForgetPassword") {
                    parentFragmentManager.addFragment(fragment = NewPasswordFragment.newInstance())
                }
                else if(response.message == "Success" && response.type == "SignUp"){
                    parentFragmentManager.addFragment(fragment = CreateProfileFragment.newInstance())
                }
            },{ throwable ->
                Log.d("Api", "API Error ${throwable.message}")
                mBinding.tvError.visibility = View.VISIBLE
            })
        )

    }

    private fun setupCodeInputListeners() {
        for (i in codeInputs.indices) {
            codeInputs[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 1) { // Khi nhập một ký tự
                        if (i < codeInputs.size - 1) {
                            codeInputs[i + 1].requestFocus() // Chuyển sang ô tiếp theo
                        }
                    } else if (count == 0 && before == 1) { // Khi xóa một ký tự
                        if (i > 0) {
                            codeInputs[i - 1].requestFocus() // Quay lại ô trước đó
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // Đảm bảo chỉ có 1 ký tự trong mỗi ô
                    if (s?.length ?: 0 > 1) {
                        s?.delete(1, s.length) // Xóa các ký tự dư
                    }
                }
            })

            codeInputs[i].setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (codeInputs[i].text?.isEmpty() == true && i > 0) {
                        codeInputs[i - 1].text?.clear() // Xóa ô trước đó
                        codeInputs[i - 1].requestFocus() // Quay lại ô trước đó
                    }
                }
                false
            }
        }
    }


}