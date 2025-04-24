package com.example.bookingmedicalapp.ui.doctors

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.PrescriptionAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentPreliminaryBinding
import com.example.bookingmedicalapp.model.*
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable

internal class PreliminaryFragment : BaseDataBindingFragment<FragmentPreliminaryBinding, PreliminaryViewModel>() {

    private lateinit var mainViewModel: MainViewModel
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var prescriptionAdapter: PrescriptionAdapter

    var checkAdd: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance() =
            PreliminaryFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_preliminary

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            onBackFragmentPressed()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        setupRecyclerView()
        callApiShow()
        addPrescriptions()
        updatePreliminary()
    }
    private fun setupRecyclerView() {
        prescriptionAdapter = PrescriptionAdapter(
            onChangeClick = { prescription ->
                callApiUpdatePrescriptions(prescription.prescription_id, prescription)
            },
            onDeleteClick = { prescription ->
                callApiDeletePrescriptions(prescription.prescription_id)
            }
        )
        mBinding.rcvPrescription.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = prescriptionAdapter
        }
    }

    private fun callApiShow(){
        compositeDisposable.add(
            repository.preliminaryDetail(AppointmentDetailRequest(mainViewModel.save_appointment_id))
                .subscribe({ response->
                    Log.d("API","API Response: $response")
                    mBinding.edtPreliminary.setText(response.data.preliminary_diagnosis)
                    mBinding.edtConclusions.setText(response.data.final_conclusion)
                    mBinding.edtRecommendations.setText(response.data.recommendations)
                    prescriptionAdapter.submitList(response.data.prescriptionDetails.toMutableList())
                },{ throwable ->
                    Log.e("API","API ERROR: $throwable")
                })
        )
    }
    private fun callApiAddPrescriptions() {
        val durationText = mBinding.edtDuration.text.toString()
        val duration = durationText.toIntOrNull()

        if (duration == null) {
            Toast.makeText(requireContext(), "Vui lòng nhập số hợp lệ cho thời gian sử dụng", Toast.LENGTH_SHORT).show()
            return
        }

        compositeDisposable.add(
            repository.addPrescriptions(
                AddPrescriptionsRequest(
                    mainViewModel.save_appointment_id,
                    mBinding.edtMedicineName.text.toString(),
                    mBinding.edtDosage.text.toString(),
                    mBinding.edtUsageInstruction.text.toString(),
                    duration
                )
            ).subscribe({ response ->
                Log.d("API", "API Response: $response")
                if (response.message == "Success")
                {
                    checkAdd = true
                    Toast.makeText(requireContext(), "Thêm đơn thuốc thành công", Toast.LENGTH_SHORT).show()
                    mBinding.cvAddMedicine.visibility = View.GONE
                    callApiShow()
                }
            }, { throwable ->
                Toast.makeText(requireContext(), "Thiếu thông tin", Toast.LENGTH_SHORT).show()
                Log.e("API", "API ERROR: $throwable")
            })
        )
    }

    private fun addPrescriptions(){
        mBinding.btnAddPrescription.setOnClickListener {
            mBinding.cvAddMedicine.visibility = View.VISIBLE
            mBinding.btnAdd.setOnClickListener {
                callApiAddPrescriptions()
            }
        }
    }

    private fun callApiUpdatePreliminary(){
        compositeDisposable.add(
            repository.updatePreliminary(UpdatePreliminaryRequest(
                mainViewModel.save_appointment_id,
                mBinding.edtPreliminary.text.toString(),
                mBinding.edtConclusions.text.toString(),
                mBinding.edtRecommendations.text.toString()
            )).subscribe({ response ->
                Log.d("API", "API Response: $response")
                Toast.makeText(requireContext(), "Cập nhật chẩn đoán bệnh thành công", Toast.LENGTH_SHORT).show()
            },{throwable ->
                Log.e("API", "API ERROR: $throwable")
            })
        )
    }
    private fun updatePreliminary(){
        mBinding.btnUpdatePreliminary.setOnClickListener {
            callApiUpdatePreliminary()
        }
    }
    private fun callApiUpdatePrescriptions(prescription_id: Int, prescription: PrescriptionDetails) {
        compositeDisposable.add(
            repository.updatePrescriptions(
                UpdatePrescriptionsRequest(
                    prescription_id,
                    prescription.medicine_name,
                    prescription.dosage,
                    prescription.usage_instruction,
                    prescription.duration
                )
            ).subscribe({ response ->
                Log.d("API", "API Response: $response")
                Toast.makeText(requireContext(), "Cập nhật đơn thuốc thành công", Toast.LENGTH_SHORT).show()
                callApiShow() // Làm mới dữ liệu sau khi cập nhật
            }, { throwable ->
                Log.e("API", "API ERROR: $throwable")
                Toast.makeText(requireContext(), "Lỗi khi cập nhật đơn thuốc", Toast.LENGTH_SHORT).show()
            })
        )
    }

    private fun callApiDeletePrescriptions(prescription_id: Int) {
        compositeDisposable.add(
            repository.deletePrescriptions(PrescriptionsRequest(prescription_id))
                .subscribe({ response ->
                    Log.d("API", "API Response: $response")
                    Toast.makeText(requireContext(), "Xóa đơn thuốc thành công", Toast.LENGTH_SHORT).show()
                    callApiShow() // Làm mới dữ liệu sau khi xóa
                }, { throwable ->
                    Log.e("API", "API ERROR: $throwable")
                    Toast.makeText(requireContext(), "Lỗi khi xóa đơn thuốc", Toast.LENGTH_SHORT).show()
                })
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear() // Clear disposables to prevent memory leaks
    }
}