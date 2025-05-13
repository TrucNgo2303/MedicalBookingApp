package com.example.bookingmedicalapp.ui.patients

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
import com.example.bookingmedicalapp.databinding.FragmentMedicalReportBinding
import com.example.bookingmedicalapp.model.AppointmentIdRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

internal class MedicalReportFragment : BaseDataBindingFragment<FragmentMedicalReportBinding, MedicalReportViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

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
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        onLeftIconClick()
        callApi()
    }

    private fun callApi(){
        val appointmentId = mainViewModel.appointment_id
        Log.d("API", "appointment_id is ${mainViewModel.appointment_id}")
        if (appointmentId == null) {
            Log.e("API", "appointment_id is null")
            return
        }
        Log.d("API", "Kiểu dữ liệu của appointmentId: ${appointmentId!!::class.qualifiedName}")

        val request = AppointmentIdRequest(appointmentId)
        compositeDisposable.add(
            repository.getAppointmentPatientDetail(request)
                .subscribe({ response ->
                    Log.d("API", "API Appointment Response: $response")

                    val data = response.data

                    Glide.with(mBinding.root.context)
                        .load(data.doctor_avatar)
                        .into(mBinding.imvAvatar)
                    mBinding.tvDoctorName.text = data.doctor_name
                    mBinding.tvPatientName.text = data.patient_name
                    mBinding.tvSpecialist.text = data.doctor_specialist

                    val inputFormatDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    val date = inputFormatDate.parse(data.appointment_date)
                    val formattedDate = outputFormatDate.format(date)

                    mBinding.tvDate.text = formattedDate
                    val inputFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val outputFormatTime = SimpleDateFormat("h:mm a", Locale("vi", "VN"))

                    val time = inputFormatTime.parse(data.appointment_time)
                    val formattedTime = outputFormatTime.format(time) // sẽ là "9:30 SA" hoặc "1:30 CH"

                    mBinding.tvTime.text = formattedTime

                    mBinding.tvNote.text = data.reason ?: "Không có"
                    mBinding.tvStatus.text = when (data.status) {
                        "Pending" -> {
                            mBinding.btnCancel.visibility = View.VISIBLE
                            "Chờ xác nhận"
                        }
                        "Confirmed" -> {
                            mBinding.btnCancel.visibility = View.VISIBLE
                            "Sắp tới"
                        }
                        "Completed" -> {
                            mBinding.btnRate.visibility = View.VISIBLE
                            "Đã hoàn thành"
                        }
                        "Cancelled" -> "Đã hủy"
                        else -> "Không xác định"
                    }
                    mBinding.tvType.text = when (data.is_online){
                        1 -> "Trực tuyến"
                        2 -> "Trực tiếp"
                        else -> "Không xác định"
                    }
                    mBinding.tvFee.text = "${data.consultation_fee} VNĐ"
                    mBinding.tvPreliminary.text = data.final_conclusion?: "Chưa có kết luận bệnh"
                    mBinding.tvRecommendations.text = data.recommendations?: "Chưa có khuyến nghị"
                    mBinding.tvPrescription.text = when (data.has_prescription){
                        1 -> {
                            mBinding.btnSeeMedicine.visibility = View.VISIBLE
                            "Đã có đơn thuốc"
                        }
                        0 -> "Chưa có đơn thuốc"
                        else -> "Không xác định"
                    }


                },{throwable->
                    Log.e("API", "API ERROR: $throwable")
                })
        )
    }
}