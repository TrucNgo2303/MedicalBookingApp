package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentBillBinding
import com.example.bookingmedicalapp.model.AppointmentRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

internal class BillFragment : BaseDataBindingFragment<FragmentBillBinding, BillViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            BillFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_bill

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupBill()
        mBinding.btnConfirm.setOnClickListener {
            callApi()
        }
    }
    private fun setupBill(){
        Log.d("MainVM","Load")
        Log.d("MainVM","doctor_name: ${mainViewModel.doctor_name}")
        Log.d("MainVM","doctor_specialy: ${mainViewModel.doctor_specialy}")
        Log.d("MainVM","dateChoose: ${mainViewModel.dateChoose}")
        Log.d("MainVM","timeChoose: ${mainViewModel.timeChoose}")
        Log.d("MainVM","noteBill: ${mainViewModel.noteBill}")

        mBinding.tvDrName.text = mainViewModel.doctor_name
        mBinding.tvSpecialist.text = mainViewModel.doctor_specialy
        mBinding.tvTime.text = mainViewModel.timeChoose

        val feeString = mainViewModel.fee  // Ví dụ: "300000.00"
        val feeDouble = feeString?.toDoubleOrNull() ?: 0.0  // đảm bảo an toàn

        val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        val formattedFee = numberFormat.format(feeDouble) + " VNĐ"

        mBinding.tvFee.text = formattedFee

        val originalDate = mainViewModel.dateChoose // Giả sử là "2025-04-12"
        val parts = originalDate?.split("-") // Tách theo dấu "-"
        if (parts != null) {
            if (parts.size == 3) {
                val formattedDate = "${parts?.get(2)}/${parts?.get(1)}/${parts?.get(0)}" // dd/mm/yyyy
                mBinding.tvDate.text = formattedDate
            } else {
                mBinding.tvDate.text = "Định dạng ngày sai"
            }
        }


        if( mainViewModel.noteBill == "")
        {
            mBinding.tvNote.text = "Không có"
        }else{
            mBinding.tvNote.text = mainViewModel.noteBill
        }

        Glide.with(mBinding.root.context)
            .load(mainViewModel.doctor_avatar)
            .into(mBinding.imvAvatarDoctor)
    }

    private fun callApi(){
        val feeString = mainViewModel.fee
        val feeDouble = feeString?.toDouble()
        val inputTime = mainViewModel.timeChoose
        val inputFormat = SimpleDateFormat("h:mm a", Locale("vi", "VN"))
        val outputFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN"))

        val convertedTime = outputFormat.format(inputFormat.parse(inputTime)!!)
        compositeDisposable.add(
            repository.createAppointment(AppointmentRequest(
                mainViewModel.patientId,
                mainViewModel.doctor_id_main.value,
                mainViewModel.dateChoose,
                convertedTime,
                "Pending",
                mainViewModel.noteBill,
                true,
                feeDouble,
                true
            )).subscribe({  response ->
                Log.d("API", "API Response: $response")
                if (response.message == "Success") {
                    Toast.makeText(requireContext(), "Đặt lịch thành công", Toast.LENGTH_SHORT).show()

                    // Delay 3 giây rồi chuyển đến PatientHomeFragment
                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.addWithNavFragment(fragment = PatientHomeFragment.newInstance())
                    }, 3000) // 3000 milliseconds = 3 giây
                }
            },{ throwable ->
                Log.e("API", "API ERROR: $throwable")
            })
        )
    }
}