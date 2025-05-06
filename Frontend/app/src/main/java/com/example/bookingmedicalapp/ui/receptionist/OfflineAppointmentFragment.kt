package com.example.bookingmedicalapp.ui.receptionist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentOfflineAppointmentBinding
import com.example.bookingmedicalapp.model.AddWaitingRequest
import com.example.bookingmedicalapp.model.CheckDoctorRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


internal class OfflineAppointmentFragment : BaseDataBindingFragment<FragmentOfflineAppointmentBinding, OfflineAppointmentViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    val timeSlots = listOf(
        "8:00 SA", "8:30 SA", "9:00 SA", "9:30 SA", "10:00 SA", "10:30 SA", "11:00 SA",
        "1:00 CH", "1:30 CH", "2:00 CH", "2:30 CH", "3:00 CH", "3:30 CH", "4:00 CH", "4:30 CH"
    )
    private var selectedDoctorName: String? = null
    private var selectedDoctorId: Int? = null
    private var selectedSpecialist: String? = null
    private var selectedTime: String? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            OfflineAppointmentFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_offline_appointment

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        mBinding.tvChooseSpecialist.setOnClickListener {
            callApiAndShowPopup(it)
        }
        mBinding.tvChooseTime.setOnClickListener {
            showPopupMenu(it,timeSlots)
        }
        mBinding.btnCheck.setOnClickListener {
            val specialist = selectedSpecialist
            val time = selectedTime
            if (specialist != null && time != null) {
                callApiCheckDoctor(specialist, time)
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn chuyên khoa và giờ", Toast.LENGTH_SHORT).show()
            }
        }
        mBinding.btnAdd.setOnClickListener {
            callApiAddWaiting()
        }
    }
    private fun showPopupMenu(anchor: View, items: List<String>) {
        val popup = PopupMenu(anchor.context, anchor)
        items.forEachIndexed { index, item ->
            popup.menu.add(0, index, index, item)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val selectedName = items[menuItem.itemId]
            (anchor as? TextView)?.text = selectedName

            when (anchor.id) {
                R.id.tv_choose_specialist -> selectedSpecialist = selectedName
                R.id.tv_choose_time -> {
                    selectedTime = convertTo24HourFormat(selectedName)
                }
            }

            true
        }

        popup.show()
    }
    private fun callApiAndShowPopup(view: View) {
        compositeDisposable.add(
            repository.allSpecialist()
                .subscribe({ response ->
                    // Lấy danh sách specialist_name từ response
                    val specialistNames = response.data.map { it.specialist_name ?: "Không xác định" }

                    // Gọi hàm để hiển thị PopupMenu
                    showPopupMenu(view, specialistNames)
                }, { throwable ->
                    Log.e("API", "API ERROR: $throwable")
                })
        )
    }
    private fun callApiCheckDoctor(specialist_name: String, appointment_time: String) {
        compositeDisposable.add(
            repository.checkDoctorSchedule(CheckDoctorRequest(specialist_name, appointment_time))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("API", "API Response: $response")

                    val doctors = response.data.filter { it.full_name != null && it.doctor_id != null }

                    if (doctors.isNotEmpty()) {
                        val doctorNames = doctors.map { it.full_name!! }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, doctorNames)
                        mBinding.lvDoctors.adapter = adapter
                        mBinding.lvDoctors.choiceMode = ListView.CHOICE_MODE_SINGLE
                        mBinding.lvDoctors.visibility = View.VISIBLE

                        mBinding.lvDoctors.setOnItemClickListener { _, _, position, _ ->
                            selectedDoctorName = doctors[position].full_name
                            selectedDoctorId = doctors[position].doctor_id
                            mBinding.lvDoctors.setItemChecked(position, true)
                            Toast.makeText(requireContext(), "Đã chọn bác sĩ: $selectedDoctorName (ID: $selectedDoctorId)", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không có bác sĩ khả dụng", Toast.LENGTH_SHORT).show()
                    }

                }, { throwable ->
                    Log.e("API", "API ERROR: $throwable")
                })
        )
    }
    private fun convertTo24HourFormat(timeStr: String): String {
        val parts = timeStr.split(" ")
        if (parts.size != 2) return timeStr // fallback nếu sai định dạng

        val time = parts[0]
        val ampm = parts[1]

        val timeParts = time.split(":")
        if (timeParts.size != 2) return timeStr

        var hour = timeParts[0].toInt()
        val minute = timeParts[1]

        if (ampm == "CH" && hour < 12) {
            hour += 12
        } else if (ampm == "SA" && hour == 12) {
            hour = 0
        }

        return String.format("%02d:%s", hour, minute)
    }
    private fun callApiAddWaiting(){
        compositeDisposable.add(
            repository.addWaiting(AddWaitingRequest(
                mBinding.edtPatientName.text.toString(),
                mBinding.edtPhoneNumber.text.toString(),
                mBinding.edtAddress.text.toString(),
                convertDateOfBirthFormat(mBinding.edtDateOfBirth.text.toString()),
                selectedDoctorId,
                selectedTime
            )).subscribe({  response ->
                Log.d("API", "API Response: $response")
                Toast.makeText(requireContext(),"Thêm thành công", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    parentFragmentManager.popBackStack()
                }, 5000)
            },{throwable ->
                Log.e("API", "API ERROR: $throwable")
                Toast.makeText(requireContext(),"Thiếu thông tin", Toast.LENGTH_SHORT).show()
            }))
    }
    private fun convertDateOfBirthFormat(inputDateStr: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(inputDateStr)
            outputFormat.format(date)
        } catch (e: Exception) {
            null // hoặc trả về "" nếu bạn muốn
        }
    }
}