package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.ViewPagerAppointmentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentPatientAppointmentBinding
import com.google.android.material.tabs.TabLayout

internal class PatientAppointmentFragment : BaseDataBindingFragment<FragmentPatientAppointmentBinding, PatientAppointmentViewModel>() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: ViewPagerAppointmentAdapter

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientAppointmentFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_patient_appointment

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        tabLayout = mBinding.tabLayout
        viewPager2 = mBinding.viewPager

        adapter = ViewPagerAppointmentAdapter(parentFragmentManager,lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Chờ xác nhận"))
        tabLayout.addTab(tabLayout.newTab().setText("Sắp tới"))
        tabLayout.addTab(tabLayout.newTab().setText("Đã hoàn thành"))
        tabLayout.addTab(tabLayout.newTab().setText("Đã hủy"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })


    }
}