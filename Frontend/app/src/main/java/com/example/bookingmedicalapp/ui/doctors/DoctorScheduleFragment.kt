package com.example.bookingmedicalapp.ui.doctors

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.DoctorAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentDoctorScheduleBinding
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
            Log.d("API","Selected Date: $selectedDate")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}