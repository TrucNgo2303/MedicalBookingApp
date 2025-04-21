package com.example.bookingmedicalapp.ui.patients.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.AppointmentNotiAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentCompletedBinding
import com.example.bookingmedicalapp.model.AppointmentNotiItem

internal class CompletedFragment : BaseDataBindingFragment<FragmentCompletedBinding, CompletedViewModel>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentNotiAdapter

    companion object {
        @JvmStatic
        fun newInstance() =
            CompletedFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_completed

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        TODO("Not yet implemented")
    }

    override fun initialize() {
        recyclerView = mBinding.rcvCompleted
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val upcomingItem = listOf(
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
            AppointmentNotiItem("Thứ 5 Ngày 6/3/2025", " - 10:00", R.drawable.default_avatar,"Nguyễn Quốc Huy","Nội Khoa","300 000 VND"),
        )

        adapter = AppointmentNotiAdapter(upcomingItem,"Bạn đã hoàn thành lịch khám", false)
        recyclerView.adapter = adapter
    }
}