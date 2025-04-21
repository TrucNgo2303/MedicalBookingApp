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
import com.example.bookingmedicalapp.databinding.FragmentBillBinding
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable

internal class BillFragment : BaseDataBindingFragment<FragmentBillBinding, BillViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            BillFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_bill

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
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupBill()
    }
    private fun setupBill(){
        Log.d("MainVM","Load")
        Log.d("MainVM","doctor_name: ${mainViewModel.doctor_name}")
        Log.d("MainVM","doctor_specialy: ${mainViewModel.doctor_specialy}")
        Log.d("MainVM","dateChoose: ${mainViewModel.dateChoose}")
        Log.d("MainVM","timeChoose: ${mainViewModel.timeChoose}")
        Log.d("MainVM","noteBill: ${mainViewModel.noteBill}")

        mBinding.tvDrName.text = mainViewModel.doctor_name
        mBinding.tvSpecialist.text = mainViewModel.doctor_specialy
        mBinding.tvTime.text = mainViewModel.timeChoose

        val originalDate = mainViewModel.dateChoose // Giả sử là "2025-04-12"
        val parts = originalDate?.split("-") // Tách theo dấu "-"
        if (parts != null) {
            if (parts.size == 3) {
                val formattedDate = "${parts?.get(2)}/${parts?.get(1)}/${parts?.get(0)}" // dd/mm/yyyy
                mBinding.tvDate.text = formattedDate
            } else {
                mBinding.tvDate.text = "Định dạng ngày sai"
            }
        }


        if( mainViewModel.noteBill == "")
        {
            mBinding.tvNote.text = "Không có"
        }else{
            mBinding.tvNote.text = mainViewModel.noteBill
        }

        Glide.with(mBinding.root.context)
            .load(mainViewModel.doctor_avatar)
            .into(mBinding.imvAvatarDoctor)
    }
}