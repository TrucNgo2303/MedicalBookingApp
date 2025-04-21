package com.example.bookingmedicalapp.ui.signupsignin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentCreateProfileBinding
import com.example.bookingmedicalapp.model.CreateProfileRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addFragment
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import java.util.*

internal class CreateProfileFragment : BaseDataBindingFragment<FragmentCreateProfileBinding, CreateProfileViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private var insertGender: String = "Other"


    private var gender = listOf("Nam", "Nữ", "Khác")

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateProfileFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_create_profile

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        mBinding.tvDateOfBirth.setOnClickListener {
            showDatePicker()
        }
        mBinding.tvGender.setOnClickListener {
            chooseGender()
        }
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mBinding.btnSave.setOnClickListener {
            callApi()
        }

    }

    private fun callApi(){
        val dateOfBirth = mBinding.tvDateOfBirth.text.toString().trim()

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày sinh!", Toast.LENGTH_SHORT).show()
            return
        }
        compositeDisposable.add(
            repository.createProfile(CreateProfileRequest(
                mainViewModel.email,
                mainViewModel.password,
                mainViewModel.full_name,
                mBinding.edtPhoneNumber.text.toString().trim(),
                dateOfBirth,
                insertGender,
                mBinding.edtAddress.text.toString().trim(),
                ""
            )).subscribe({response ->
                Log.d("Api", "Api Response: $response")
                if(response.message == "Success"){
                    parentFragmentManager.addFragment(fragment = SignInFragment.newInstance())
                }

            },{throwable ->
                Log.d("Api", "API Error ${throwable.message}")

            })
        )
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                mBinding.tvDateOfBirth.text = formattedDate
            },
            year, month, day
        )

        datePickerDialog.show()
    }
    private fun chooseGender() {
        val options = arrayOf("Nam", "Nữ", "Khác")
        AlertDialog.Builder(requireContext())
            .setTitle("Chọn giới tính")
            .setItems(options) { _, which ->
                mBinding.tvGender.text = options[which]
                // Cập nhật giá trị insertGender khi chọn giới tính
                insertGender = when (options[which]) {
                    "Nam" -> "Male"
                    "Nữ" -> "Female"
                    else -> "Other"
                }
            }
            .show()
    }

}