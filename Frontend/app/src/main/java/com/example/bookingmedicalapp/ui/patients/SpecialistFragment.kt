package com.example.bookingmedicalapp.ui.patients

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.SpecialistAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.BaseViewModel
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentSpecialistBinding
import com.example.bookingmedicalapp.model.AllSpecialistResponse
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable

internal class SpecialistFragment : BaseDataBindingFragment<FragmentSpecialistBinding, SpecialistViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var specialistAdapter: SpecialistAdapter
    private var specialistList: MutableList<AllSpecialistResponse> = mutableListOf()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            SpecialistFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_specialist

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
        setupRecyclerView()
        callApi()
    }
    private fun setupRecyclerView() {
        specialistAdapter = SpecialistAdapter(specialistList) { specialist ->
            mainViewModel.specialistId.postValue(specialist.specialist_id)
            Toast.makeText(context, "Chọn chuyên khoa: ${specialist.specialist_name}", Toast.LENGTH_SHORT).show()
            parentFragmentManager.addWithNavFragment(fragment = SpecialistDetailFragment.newInstance())
            Log.d("Specialist","Specialist_id: ${mainViewModel.specialistId}")
        }
        mBinding.rcvAllSpecialist.apply {
            adapter = specialistAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun callApi() {
        compositeDisposable.add(
            repository.allSpecialist()
                .subscribe({ response ->
                    Log.d("API", "API Response: $response")

                    // Kiểm tra dữ liệu nhận được
                    response.data?.let { newList ->
                        specialistList.clear()  // Xóa dữ liệu cũ
                        specialistList.addAll(newList)  // Cập nhật dữ liệu mới
                        specialistAdapter.notifyDataSetChanged()  // Cập nhật RecyclerView
                    }
                }, { throwable ->
                    Log.e("API", "API ERROR: $throwable")
                })
            )
        }
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}