package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.common.Constants
import com.example.bookingmedicalapp.databinding.FragmentPatientHomeBinding
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addFragment
import com.example.bookingmedicalapp.utils.addFragmentWithAnimation
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

internal class PatientHomeFragment : BaseDataBindingFragment<FragmentPatientHomeBinding, PatientHomeViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            PatientHomeFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_patient_home

    override fun onBackFragmentPressed() {

    }

    override fun onLeftIconClick() {
    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mBinding.tvSearch.setOnClickListener {
            parentFragmentManager.addFragmentWithAnimation(R.id.fragment_container,fragment = SearchFragment.newInstance())
            Toast.makeText(requireContext(), "TextView clicked!", Toast.LENGTH_SHORT).show()
        }
        val sharedPref = TokenAction(requireContext())
        val token = sharedPref.getToken()
        Log.d("API","Token hiện tại: $token")
        callAllAPIs()

        mBinding.btnSeeAll.setOnClickListener {
            parentFragmentManager.addWithNavFragment(fragment = SpecialistFragment.newInstance())
        }
    }

    private fun callAllAPIs() {
        compositeDisposable.add(
            Observable.zip(
                repository.getHomePatientInfo(),
                repository.getSpecialistHome(),
                repository.topDoctor()
            ) { patientResponse, specialistResponse, doctorResponse ->
                Triple(patientResponse.data, specialistResponse.data, doctorResponse.data)
            }.subscribe({ (patientInfo, specialistList, doctorList) ->
                Log.d("API", "Patient Info: $patientInfo")
                Log.d("API", "Specialist List: $specialistList")
                Log.d("API", "Doctor List: $doctorList")

                // Cập nhật thông tin bệnh nhân
                patientInfo.avatar?.let {
                    Glide.with(mBinding.root.context).load(it).into(mBinding.imvAvatar)
                }
                val fullName = patientInfo.full_name
                val lastName = fullName?.substringAfterLast(" ") // Lấy từ cuối cùng sau dấu cách
                mBinding.tvName.text = lastName

                // Cập nhật danh sách chuyên khoa
                val specialistTextView = listOf(
                    mBinding.tvSpecialist1, mBinding.tvSpecialist2, mBinding.tvSpecialist3
                )
                val specialistImageView = listOf(
                    mBinding.imvSpecialist1, mBinding.imvSpecialist2, mBinding.imvSpecialist3
                )
                specialistList.forEachIndexed { index, specialist ->
                    if (index < specialistTextView.size) {
                        specialistTextView[index].text = specialist.specialist_name
                        Glide.with(mBinding.root.context)
                            .load(specialist.icon)
                            .into(specialistImageView[index])
                        when (index) {
                            0 -> mainViewModel.specialist_id_1.postValue(specialist.specialist_id)
                            1 -> mainViewModel.specialist_id_2.postValue(specialist.specialist_id)
                            2 -> mainViewModel.specialist_id_3.postValue(specialist.specialist_id)
                        }
                    }
                }
                mBinding.cvSpecialist1.setOnClickListener {
                    mainViewModel.specialist_id_1.observe(viewLifecycleOwner) { specialist ->
                        mainViewModel.specialistId.postValue(specialist)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = SpecialistDetailFragment.newInstance())
                }
                mBinding.cvSpecialist2.setOnClickListener {
                    mainViewModel.specialist_id_2.observe(viewLifecycleOwner) { specialist ->
                        mainViewModel.specialistId.postValue(specialist)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = SpecialistDetailFragment.newInstance())
                }
                mBinding.cvSpecialist3.setOnClickListener {
                    mainViewModel.specialist_id_3.observe(viewLifecycleOwner) { specialist ->
                        mainViewModel.specialistId.postValue(specialist)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = SpecialistDetailFragment.newInstance())
                }

                // Cập nhật danh sách bác sĩ
                val nameTextViews = listOf(
                    mBinding.tvName1, mBinding.tvName2, mBinding.tvName3, mBinding.tvName4, mBinding.tvName5
                )
                val specialistTextViews = listOf(
                    mBinding.tvSpecialistDoctor1, mBinding.tvSpecialistDoctor2, mBinding.tvSpecialistDoctor3, mBinding.tvSpecialistDoctor4, mBinding.tvSpecialistDoctor5
                )
                val qualificationTextViews = listOf(
                    mBinding.tvQualification1, mBinding.tvQualification2, mBinding.tvQualification3, mBinding.tvQualification4, mBinding.tvQualification5
                )
                val averageStarTextViews = listOf(
                    mBinding.tvRating1, mBinding.tvRating2, mBinding.tvRating3, mBinding.tvRating4, mBinding.tvRating5
                )
                val avatarImageViews = listOf(
                    mBinding.civAvatar1, mBinding.civAvatar2, mBinding.civAvatar3, mBinding.civAvatar4, mBinding.civAvatar5
                )

                doctorList.forEachIndexed { index, doctor ->
                    if (index < nameTextViews.size) {
                        nameTextViews[index].text = doctor.full_name
                        specialistTextViews[index].text = doctor.specialty
                        qualificationTextViews[index].text = doctor.qualification
                        averageStarTextViews[index].text = doctor.average_star
                        Glide.with(mBinding.root.context)
                            .load(doctor.avatar)
                            .into(avatarImageViews[index])
                        when (index) {
                            0 -> mainViewModel.doctor_id_1.postValue(doctor.doctor_id)
                            1 -> mainViewModel.doctor_id_2.postValue(doctor.doctor_id)
                            2 -> mainViewModel.doctor_id_3.postValue(doctor.doctor_id)
                            3 -> mainViewModel.doctor_id_4.postValue(doctor.doctor_id)
                            4 -> mainViewModel.doctor_id_5.postValue(doctor.doctor_id)
                        }

                    }
                }
                mBinding.cvDoctor1.setOnClickListener {
                    mainViewModel.doctor_id_1.observe(viewLifecycleOwner) { doctorId ->
                        mainViewModel.doctor_id_main.postValue(doctorId)
                    }

                    parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                }
                mBinding.cvDoctor2.setOnClickListener {
                    mainViewModel.doctor_id_2.observe(viewLifecycleOwner) { doctorId ->
                        mainViewModel.doctor_id_main.postValue(doctorId)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                }
                mBinding.cvDoctor3.setOnClickListener {
                    mainViewModel.doctor_id_3.observe(viewLifecycleOwner) { doctorId ->
                        mainViewModel.doctor_id_main.postValue(doctorId)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                }
                mBinding.cvDoctor4.setOnClickListener {
                    mainViewModel.doctor_id_4.observe(viewLifecycleOwner) { doctorId ->
                        mainViewModel.doctor_id_main.postValue(doctorId)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                }
                mBinding.cvDoctor5.setOnClickListener {
                    mainViewModel.doctor_id_5.observe(viewLifecycleOwner) { doctorId ->
                        mainViewModel.doctor_id_main.postValue(doctorId)
                    }
                    parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                }

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


}