package com.example.bookingmedicalapp.ui.doctors

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentAppointmentDetailBinding
import com.example.bookingmedicalapp.model.AppointmentDetailRequest
import com.example.bookingmedicalapp.model.AppointmentRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addFragment
import io.reactivex.disposables.CompositeDisposable
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

internal class AppointmentDetailFragment : BaseDataBindingFragment<FragmentAppointmentDetailBinding, AppointmentDetailViewModel>() {

    private lateinit var mainViewModel: MainViewModel
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()

    companion object {
        @JvmStatic
        fun newInstance() =
            AppointmentDetailFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_appointment_detail

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            onBackFragmentPressed()
        }
    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        onLeftIconClick()
        callApi()
        detailPreliminaryAndPrescriptions()
    }
    private fun callApi(){
        compositeDisposable.add(
            repository.appointmentDetail(AppointmentDetailRequest(mainViewModel.save_appointment_id)).subscribe({ response->
                Log.d("API","API Response: $response")

                if(!response.patient_avatar.isNullOrEmpty()) {
                    Glide.with(mBinding.root.context)
                        .load(response.patient_avatar)
                        .into(mBinding.imvAvatar)
                }
                mBinding.tvPatientName.text = response.patient_name
                mBinding.tvDoctorName.text = response.doctor_name
                mBinding.tvSpecialist.text = response.specialty
                val (formattedDate, formattedTime) = convertAppointmentDateTime(response.appointment_date, response.appointment_time)
                mBinding.tvDate.text = formattedDate
                mBinding.tvTime.text = formattedTime
                mBinding.tvNote.text = response.reason
                mBinding.tvStatus.text = when (response.status) {
                    "Pending" -> "Đợi xác nhận"
                    "Confirmed" -> "Sắp tới"
                    "Completed" -> {
                        mBinding.btnConfirmed.visibility = View.GONE
                        "Hoàn thành"
                    }
                    else -> {
                        mBinding.btnConfirmed.visibility = View.GONE
                        "Đã hủy"
                    }
                }

                mBinding.tvType.text = when (response.is_online) {
                    1 -> "Trực tuyến"
                    0 -> "Trực tiếp tại quầy"
                    else -> "Không xác định"
                }
                val fee = try {
                    response.consultation_fee.toDoubleOrNull() ?: 0.0
                } catch (e: NumberFormatException) {
                    Log.e("API", "Invalid consultation_fee format: ${response.consultation_fee}")
                    0.0
                }
                val decimalFormat = DecimalFormat("#,###")
                decimalFormat.decimalFormatSymbols = DecimalFormatSymbols().apply {
                    groupingSeparator = ','
                    decimalSeparator = '.'
                }
                val formattedFee = decimalFormat.format(fee)
                mBinding.tvFee.text = "$formattedFee VNĐ"
                mBinding.tvPreliminary.text = when (response.has_preliminary_diagnosis){
                    1 -> "Đã chẩn đoán"
                    0 -> "Chưa chẩn đoán"
                    else -> "Không xác định"
                }
                mBinding.tvPrescription.text = when (response.has_prescription){
                    1 -> "Đã kê đơn thuốc"
                    0 -> "Chưa có đơn thuốc"
                    else -> "Không xác định"
                }


            },{ throwable ->
                Log.e("API","API ERROR: $throwable")
            }

            )
        )
    }
    private fun convertAppointmentDateTime(date: String?, time: String?): Pair<String, String> {
        // Định dạng ngày đầu vào và đầu ra
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Định dạng giờ đầu vào và đầu ra
        val inputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputTimeFormat = SimpleDateFormat("h:mm a", Locale("vi"))

        // Convert date
        val formattedDate = try {
            date?.let {
                val parsedDate = inputDateFormat.parse(it)
                parsedDate?.let { dateObj -> outputDateFormat.format(dateObj) } ?: ""
            } ?: ""
        } catch (e: Exception) {
            ""
        }

        // Convert time
        val formattedTime = try {
            time?.let {
                val parsedTime = inputTimeFormat.parse(it)
                parsedTime?.let { timeObj ->
                    outputTimeFormat.format(timeObj).replace("AM", "SA").replace("PM", "CH")
                } ?: ""
            } ?: ""
        } catch (e: Exception) {
            ""
        }

        return Pair(formattedDate, formattedTime)
    }

    private fun detailPreliminaryAndPrescriptions(){
        mBinding.tvArrow1.setOnClickListener {
            parentFragmentManager.addFragment(fragment = PreliminaryFragment.newInstance())
        }
        mBinding.tvArrow2.setOnClickListener {
            parentFragmentManager.addFragment(fragment = PreliminaryFragment.newInstance())
        }
    }
    override fun onResume() {
        super.onResume()
        callApi() // gọi lại API mỗi lần màn hình được hiển thị lại
    }
}