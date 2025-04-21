package com.example.bookingmedicalapp.ui.patients

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.CalendarAdapter
import com.example.bookingmedicalapp.adapter.ChooseTimeAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentBookAppointmentBinding
import com.example.bookingmedicalapp.model.DateModel
import com.example.bookingmedicalapp.model.DoctorDetailRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

internal class BookAppointmentFragment : BaseDataBindingFragment<FragmentBookAppointmentBinding, BookAppointmentViewModel>() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter1: CalendarAdapter
    private var bookedAppointments: List<String> = emptyList() // Lưu danh sách lịch hẹn (yyyy-MM-dd h:mm a)

    private var selectedDate = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, 1)
    }.let {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.time)
    }
    private var timeChoose = ""
    private var dateChoose: String = getTomorrowDate()

    private fun getTomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BookAppointmentFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_book_appointment

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recyclerView1 = mBinding.recyclerViewDate
        recyclerView2 = mBinding.recyclerViewTime
        onLeftIconClick()
        setupDoctor()
        setupRecyclerView()
        callApi() // Gọi API trước, sau đó setupTimeRecyclerView() sẽ được gọi trong callback
    }

    private fun setupDoctor() {
        mBinding.tvName.text = mainViewModel.doctor_name
        mBinding.tvSpecialist.text = mainViewModel.doctor_specialy
        mBinding.tvQualification.text = mainViewModel.doctor_qualification
        Glide.with(mBinding.root.context)
            .load(mainViewModel.doctor_avatar)
            .into(mBinding.imvAvatar)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH) + 1
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentYear = calendar.get(Calendar.YEAR)

        val daysList = getNextDays(currentDay, currentMonth, currentYear, 15)
        adapter1 = CalendarAdapter(daysList, selectedDate) { newSelectedDate ->
            selectedDate = newSelectedDate
            dateChoose = newSelectedDate
            (recyclerView2.adapter as? ChooseTimeAdapter)?.updateSelectedDate(newSelectedDate)
            Log.d("SelectedDate", "Ngày được chọn: $newSelectedDate")
            adapter1.notifyDataSetChanged()
        }
        recyclerView1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.adapter = adapter1
    }

    private fun setupTimeRecyclerView() {
        val timeSlots = listOf(
            "8:00 SA", "8:30 SA", "9:00 SA", "9:30 SA", "10:00 SA", "10:30 SA", "11:00 SA",
            "1:00 CH", "1:30 CH", "2:00 CH", "2:30 CH", "3:00 CH", "3:30 CH", "4:00 CH", "4:30 CH"
        )
        val adapter2 = ChooseTimeAdapter(timeSlots, bookedAppointments) { selectedTime ->
            timeChoose = selectedTime
            Toast.makeText(requireContext(), "Selected: $selectedTime", Toast.LENGTH_SHORT).show()
            Log.d("SelectedTime", "Giờ được chọn: $timeChoose")
        }
        recyclerView2.adapter = adapter2
        recyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Cập nhật ngày mặc định cho ChooseTimeAdapter để tô xám ngay từ đầu
        adapter2.updateSelectedDate(selectedDate)

        mBinding.btnConfirm.setOnClickListener {
            if (timeChoose.isEmpty()) {
                Toast.makeText(requireContext(), "Bạn cần phải chọn thời gian", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("SelectedTime", "Giờ được chọn: $timeChoose")
                mainViewModel.timeChoose = timeChoose
                mainViewModel.dateChoose = dateChoose
                mainViewModel.noteBill = mBinding.edtNote.text.toString()

                Log.d("MainVM", "Save")
                Log.d("MainVM", "doctor_name: ${mainViewModel.doctor_name}")
                Log.d("MainVM", "doctor_specialy: ${mainViewModel.doctor_specialy}")
                Log.d("MainVM", "dateChoose: ${mainViewModel.dateChoose}")
                Log.d("MainVM", "timeChoose: ${mainViewModel.timeChoose}")
                Log.d("MainVM", "noteBill: ${mainViewModel.noteBill}")
                parentFragmentManager.addWithNavFragment(fragment = BillFragment.newInstance())
            }
        }
    }

    private fun getNextDays(startDay: Int, month: Int, year: Int, totalDays: Int): List<DateModel> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.YEAR, year)

        val daysList = mutableListOf<DateModel>()
        val dateFormat = SimpleDateFormat("EEE", Locale("vi", "VN"))

        for (i in 0 until totalDays) {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val fullDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val dayOfWeek = dateFormat.format(calendar.time)

            daysList.add(DateModel(dayOfWeek, day, fullDate, currentMonth))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return daysList
    }

    private fun callApi() {
        compositeDisposable.add(
            repository.checkDoctorAppointment(DoctorDetailRequest(mainViewModel.doctor_id_main.value)).subscribe({ response ->
                Log.d("API", "API Response: $response")
                bookedAppointments = response.data?.map { convertApiToLocal(it.appointment_datetime) } ?: emptyList()
                Log.d("BookedAppointments", "Danh sách lịch hẹn: $bookedAppointments")
                setupTimeRecyclerView() // Gọi setupTimeRecyclerView() sau khi có dữ liệu từ API
            }, { throwable ->
                Log.e("API", "API ERROR: $throwable")
                bookedAppointments = emptyList()
                setupTimeRecyclerView()
            })
        )
    }

    private fun convertApiToLocal(apiTime: String): String {
        val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val localFormat = SimpleDateFormat("yyyy-MM-dd h:mm a", Locale("vi", "VN"))
        val date = apiFormat.parse(apiTime)
        return date?.let { localFormat.format(it) } ?: ""
    }
}