package com.example.bookingmedicalapp.ui.patients

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.SpecialistAdapter
import com.example.bookingmedicalapp.adapter.SpecialistDetailAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentSpecialistDetailBinding
import com.example.bookingmedicalapp.model.AllSpecialistResponse
import com.example.bookingmedicalapp.model.SpecialistDetailRequest
import com.example.bookingmedicalapp.model.SpecialistDetailResponse
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable

internal class SpecialistDetailFragment : BaseDataBindingFragment<FragmentSpecialistDetailBinding, SpecialistDetailViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel
    private var doctorList: MutableList<SpecialistDetailResponse> = mutableListOf()
    private lateinit var doctorListAdapter: SpecialistDetailAdapter

    companion object {
        @JvmStatic
        fun newInstance() =
            SpecialistDetailFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_specialist_detail

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
        callApi1Specialist()
        callApi()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun callApi(){
        compositeDisposable.add(
            repository.specialistDetail(SpecialistDetailRequest(mainViewModel.specialistId.value)).subscribe({ response ->
                Log.d("API", "API Response: $response")

                response.data?.let { newList ->
                    doctorList.clear()  // Xóa dữ liệu cũ
                    doctorList.addAll(newList)  // Cập nhật dữ liệu mới
                    doctorListAdapter.notifyDataSetChanged()  // Cập nhật RecyclerView
                }

            },{ throwable ->
                Log.e("API_1", "API ERROR: $throwable")
            })
        )
    }
    private fun callApi1Specialist(){
        compositeDisposable.add(
            repository.getSpecialist(SpecialistDetailRequest(mainViewModel.specialistId.value)).subscribe({ response ->
                Log.d("API", "API Response: $response")

                mBinding.tvSpecialistName.text = response.data.specialist_name
                mBinding.tvSummary.text = response.data.summary
                mBinding.tvFee.text = response.data.consultation_fee
                Glide.with(mBinding.root.context)
                    .load(response.data.icon)
                    .placeholder(R.drawable.default_avatar) // Ảnh mặc định khi đang tải
                    .error(R.drawable.default_avatar) // Ảnh mặc định nếu lỗi
                    .into(mBinding.imvSpecialistIcon)

            },{ throwable ->
                Log.e("API_2", "API ERROR: $throwable")
            })
        )
    }
    private fun setupRecyclerView() {
        doctorListAdapter = SpecialistDetailAdapter(doctorList) { doctor ->
            mainViewModel.doctor_id_main.postValue(doctor.doctor_id)
            parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
            Toast.makeText(context, "Chọn chuyên khoa: ${doctor.full_name}", Toast.LENGTH_SHORT).show()
            Log.d("Specialist","Specialist_id: ${mainViewModel.specialistId}")
        }
        mBinding.rcv.apply {
            adapter = doctorListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}