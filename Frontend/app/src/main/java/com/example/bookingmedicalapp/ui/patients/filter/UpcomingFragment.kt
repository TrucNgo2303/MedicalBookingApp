package com.example.bookingmedicalapp.ui.patients.filter

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.AppointmentNotiAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentUpcomingBinding
import com.example.bookingmedicalapp.model.AppointmentNotiItem

internal class UpcomingFragment : BaseDataBindingFragment<FragmentUpcomingBinding, UpcomingViewModel>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentNotiAdapter

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


        val upcomingItem = listOf(
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
        )

        adapter = AppointmentNotiAdapter(upcomingItem, "Bác sĩ đã xác nhận lịch khám của bạn", true)
        recyclerView.adapter = adapter


    }
}