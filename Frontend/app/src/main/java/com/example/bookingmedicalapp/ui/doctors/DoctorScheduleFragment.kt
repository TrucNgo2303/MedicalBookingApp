package com.example.bookingmedicalapp.ui.doctors

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.DoctorAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentDoctorScheduleBinding
import com.example.bookingmedicalapp.model.AppointmentDateRequest
import com.example.bookingmedicalapp.model.DoctorAppointment
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

internal class DoctorScheduleFragment : BaseDataBindingFragment<FragmentDoctorScheduleBinding, DoctorScheduleViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var appointmentAdapter: DoctorAppointmentAdapter
    private var selectedDate: String = ""

    companion object {
        @JvmStatic
        fun newInstance() =
            DoctorScheduleFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_doctor_schedule

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            onBackFragmentPressed()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        appointmentAdapter = DoctorAppointmentAdapter()
        mBinding.rcvAppointment.adapter = appointmentAdapter
        mBinding.rcvAppointment.layoutManager = LinearLayoutManager(context)


        val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val calendar = Calendar.getInstance(vietnamTimeZone)
        val storeDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("vi", "VN"))
        selectedDate = storeDateFormat.format(calendar.time)
        Log.d("API", "Initial selectedDate: $selectedDate")

        callApi(selectedDate)

        setupCalendarView()

    }
    private fun setupCalendarView() {
        // Đặt múi giờ Việt Nam (GMT+7)
        val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val calendar = Calendar.getInstance(vietnamTimeZone)

        // Đặt ngày hiện tại cho CalendarView
        mBinding.calendar.date = calendar.timeInMillis

        // Định dạng ngày
        val displayDateFormat = SimpleDateFormat("dd/MM", Locale("vi", "VN"))
        val storeDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("vi", "VN"))

        // Khởi tạo ngày hiện tại cho tv_appointment và selectedDate
        val currentDate = calendar.time
        mBinding.tvAppointment.text = displayDateFormat.format(currentDate)
        selectedDate = storeDateFormat.format(currentDate) // Gán ngày hiện tại cho selectedDate

        // Gọi API với ngày hiện tại khi vào màn hình
        callApi(selectedDate)

        // Xử lý sự kiện khi chọn ngày
        mBinding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // CalendarView trả về tháng từ 0-11, cần tăng tháng lên 1
            val selectedCalendar = Calendar.getInstance(vietnamTimeZone).apply {
                set(year, month, dayOfMonth)
            }
            val selectedDateTime = selectedCalendar.time

            // Cập nhật tv_appointment với định dạng dd/MM
            mBinding.tvAppointment.text = displayDateFormat.format(selectedDateTime)

            // Lưu ngày với định dạng yyyy-MM-dd
            selectedDate = storeDateFormat.format(selectedDateTime)
            Log.d("API", "Selected Date: $selectedDate")

            // Gọi API với ngày được chọn
            callApi(selectedDate)
        }
    }

    private fun callApi(appointment_date: String) {
        compositeDisposable.add(
            repository.doctorSchedule(AppointmentDateRequest(appointment_date)).subscribe({ response ->
                Log.d("API", "API Response: $response")
                val appointments = response.data.map { item ->
                    // Định dạng đúng với API response: 2025-04-23T01:00:00.000Z
                    val dateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC") // API response dùng UTC
                    }.parse(item.appointment_datetime)

                    // Handle status transformation
                    DoctorAppointment(
                        appointment_id = item.appointment_id,
                        avatar = item.avatar ?: "",
                        patient_name = "${item.full_name}",
                        status = item.status,
                        date = SimpleDateFormat("dd-MM", Locale.getDefault()).format(dateTime),
                        time = SimpleDateFormat("h:mm a", Locale("vi", "VN")).format(dateTime)
                            .replace("AM", "SA").replace("PM", "CH"),
                        is_online = item.is_online
                    )
                }.sortedBy { appointment ->
                    when (appointment.status) {
                        "Confirmed" -> 1
                        "Pending" -> 2
                        "Completed" -> 3
                        "Cancelled" -> 4
                        else -> 5 // Default case for unknown statuses
                    }
                }
                appointmentAdapter.updateAppointments(appointments)
            }, { throwable ->
                Log.e("API", "API ERROR: $throwable")
                // Hiển thị thông báo lỗi nếu cần
                Toast.makeText(context, "Không có lịch hẹn nào", Toast.LENGTH_SHORT).show()
            })
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}