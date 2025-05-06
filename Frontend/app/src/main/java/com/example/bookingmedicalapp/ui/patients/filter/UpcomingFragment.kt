package com.example.bookingmedicalapp.ui.patients.filter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.AppointmentNotiAdapter
import com.example.bookingmedicalapp.adapter.DoctorAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentUpcomingBinding
import com.example.bookingmedicalapp.model.AppointmentNotiItem
import com.example.bookingmedicalapp.model.StatusRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

internal class UpcomingFragment : BaseDataBindingFragment<FragmentUpcomingBinding, UpcomingViewModel>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentNotiAdapter
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()

    companion object {
        @JvmStatic
        fun newInstance() =
            UpcomingFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_upcoming

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {

        recyclerView = mBinding.rcvUpcoming
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        callApi()
    }

    private fun callApi(){
        compositeDisposable.add(
            repository.appointmentStatus(StatusRequest("Confirmed")).subscribe({  response->
                Log.d("API", "API Response: $response")
                if (response.data.isEmpty()) {
                    mBinding.tvNull.visibility = View.VISIBLE
                } else {
                    val appointmentItems = response.data?.map { appointment ->
                        // Parse and format the date and time
                        val dateTime =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                                appointment.appointment_datetime
                            )
                        val dateFormat =
                            SimpleDateFormat("EEEE 'Ngày' d/M/yyyy", Locale("vi", "VN"))
                        val timeFormat = SimpleDateFormat(" - HH:mm", Locale.getDefault())
                        val formattedDate = dateFormat.format(dateTime ?: Date())
                        val formattedTime = timeFormat.format(dateTime ?: Date())

                        // Determine SA (Sáng) or CH (Chiều) based on hour
                        val hour =
                            SimpleDateFormat("HH", Locale.getDefault()).format(dateTime ?: Date())
                                .toInt()
                        val timeSuffix = if (hour < 12) "SA" else "CH"
                        val finalTime = "$formattedTime $timeSuffix"

                        // Return a pair of AppointmentNotiItem and its Date for sorting
                        Pair(
                            AppointmentNotiItem(
                                date = formattedDate,
                                time = finalTime,
                                avatar = appointment.doctor_avatar,
                                name = appointment.doctor_name,
                                specialist = appointment.specialist,
                                fee = "${formatFee(appointment.consultation_fee)} VND"
                            ),
                            dateTime ?: Date()
                        )
                    }?.sortedBy { it.second }?.map { it.first } ?: emptyList()

                    // Update adapter with new data
                    adapter =
                        AppointmentNotiAdapter(appointmentItems, "Lịch đã được đặt", R.color.orange)
                    recyclerView.adapter = adapter
                }
            },{ throwable->
                Log.e("API","API ERROR: $throwable")
            })
        )
    }
    private fun formatFee(fee: String): String {
        // Chuyển fee từ String sang Double
        val feeDouble = fee.toDoubleOrNull()

        return if (feeDouble != null) {
            val decimalFormat = DecimalFormat("#,###.##")
            decimalFormat.format(feeDouble)
        } else {
            fee
        }
    }
}