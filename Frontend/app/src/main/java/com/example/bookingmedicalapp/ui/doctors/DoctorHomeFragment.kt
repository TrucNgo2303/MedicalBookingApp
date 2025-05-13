package com.example.bookingmedicalapp.ui.doctors

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.DoctorAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.common.Constants
import com.example.bookingmedicalapp.databinding.FragmentDoctorHomeBinding
import com.example.bookingmedicalapp.model.DoctorAppointment
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.ui.LoadingActivity
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment
import com.example.bookingmedicalapp.utils.addWithNavDocTorFragment
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

internal class DoctorHomeFragment : BaseDataBindingFragment<FragmentDoctorHomeBinding, DoctorHomeViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var appointmentAdapter: DoctorAppointmentAdapter
    private var appointmentJob: Job? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            DoctorHomeFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_doctor_home

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        callApiNum()
        mBinding.imvAvatar.setOnClickListener {
            val sharedPref = TokenAction(requireContext())
            sharedPref.clearToken()
            // Xóa toàn bộ back stack
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val intent = Intent(requireContext(), LoadingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
    private fun setupRecyclerView() {
        appointmentAdapter = DoctorAppointmentAdapter()
        mBinding.rcvAppointmentToday.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appointmentAdapter
        }
        // Thêm xử lý click cho item
        appointmentAdapter.onItemClick = { appointmentId ->
            mainViewModel.save_appointment_id = appointmentId
            parentFragmentManager.addWithNavDocTorFragment(fragment = AppointmentDetailFragment.newInstance())
        }
    }

    private fun callApiNum() {
        compositeDisposable.add(
            repository.numOfAppointment().subscribe({ response ->
                Log.d("API", "API Response: $response")
                mBinding.tvNumberAppointment.text = response.data.number_of_appointment_today.toString()
            }, { throwable ->
                if (throwable is retrofit2.HttpException && throwable.code() == 401) {
                    val sharedPref = TokenAction(requireContext())
                    sharedPref.clearToken()
                    parentFragmentManager.addFragment(fragment = SignInFragment.newInstance())
                } else {
                    Log.e("API", "API ERROR: $throwable")
                }
            })
        )
    }

    private fun callApiTodayAppointment() {
        compositeDisposable.add(
            repository.todayAppointment().subscribe({ response ->
                Log.d("API", "API Response: $response")
                val appointments = response.data.map { item ->
                    val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .parse(item.appointment_datetime)
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
                if (throwable is retrofit2.HttpException && throwable.code() == 401) {
                    val sharedPref = TokenAction(requireContext())
                    sharedPref.clearToken()
                    parentFragmentManager.addFragment(fragment = SignInFragment.newInstance())
                } else {
                    Log.e("API", "API ERROR: $throwable")
                }
            })
        )
    }

    private fun startRepeatingAppointment() {
        appointmentJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                callApiTodayAppointment()
                delay(5_000) // đợi 30 giây
            }
        }
    }

    private fun stopRepeatingAppointment() {
        appointmentJob?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startRepeatingAppointment()
    }

    override fun onStop() {
        super.onStop()
        stopRepeatingAppointment()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}