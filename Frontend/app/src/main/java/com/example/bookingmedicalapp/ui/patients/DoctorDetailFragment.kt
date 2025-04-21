package com.example.bookingmedicalapp.ui.patients

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.CommentAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentDoctorDetailBinding
import com.example.bookingmedicalapp.model.*
import com.example.bookingmedicalapp.model.DoctorDetailRequest
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import java.text.SimpleDateFormat
import java.util.*

internal class DoctorDetailFragment : BaseDataBindingFragment<FragmentDoctorDetailBinding, DoctorDetailViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mainViewModel: MainViewModel

    companion object {
        @JvmStatic
        fun newInstance() =
            DoctorDetailFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_doctor_detail

    override fun onBackFragmentPressed() {
        parentFragmentManager.popBackStack()
    }

    override fun onLeftIconClick() {
        mBinding.imvBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        callApi()
        callApiComment()
        mBinding.btnBooking.setOnClickListener {
            parentFragmentManager.addWithNavFragment(fragment = BookAppointmentFragment.newInstance())
        }

    }
    private fun setUpRecycleView(comments: List<Comment>) {
        val commentAdapter = CommentAdapter(comments)
        mBinding.rcvComments.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rcvComments.adapter = commentAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun callApi(){
        mainViewModel.doctor_id_main.observe(viewLifecycleOwner) { doctorId ->
            Log.d("ViewModel", "Doctor ID: $doctorId")
            compositeDisposable.add(
                repository.doctorDetail(DoctorDetailRequest(doctorId)).subscribe({ response ->
                    Log.d("API", "API Response: $response")
                    val doctor = response.data
                    Glide.with(mBinding.root.context)
                        .load(doctor.avatar)
                        .into(mBinding.imvAvatarDoctor)
                    mBinding.tvDoctorName.text = "Bs. ${doctor.full_name?.trim().orEmpty()}"
                    mBinding.tvDoctorQualification.text = doctor.qualification
                    mBinding.tvRating.text = doctor.average_star
                    mBinding.tvNumberReview.text = "(${doctor.total_comments} đánh giá)"
                    mBinding.tvPatientsNumber.text = "${doctor.patient_count}+"
                    mBinding.tvExperienceNumber.text = "${doctor.experience_years}+"
                    mBinding.tvRatingNumber.text = doctor.average_star
                    val totalComments = doctor.total_comments?.toIntOrNull() ?: 0
                    mBinding.tvReviewsNumber.text = if (totalComments < 10) {
                        "$totalComments"
                    } else {
                        "${(totalComments / 10) * 10}+"
                    }
                    mBinding.tvAboutMe.text = doctor.summary

                    //Lưu vào VM cho BookAppoiment
                    mainViewModel.doctor_name = "Bs. ${doctor.full_name?.trim().orEmpty()}"
                    mainViewModel.doctor_specialy = doctor.specialty
                    mainViewModel.doctor_qualification = doctor.qualification
                    mainViewModel.doctor_avatar = doctor.avatar
                    mainViewModel.fee = doctor.consultation_fee
                },{ throwable ->
                    Log.e("API", "API ERROR: $throwable")
                })
            )
        }

    }

    private fun callApiComment() {
        val commentsList = mutableListOf<Comment>()
        mainViewModel.doctor_id_main.observe(viewLifecycleOwner) { doctorId ->
            Log.d("ViewModel", "Doctor ID: $doctorId")
            compositeDisposable.add(
                repository.getAllComments(CommentRequest(doctorId)).subscribe({ response ->
                    Log.d("API", "API Comment Response: $response")
                    if (response.data.isNotEmpty()) {
                        response.data.forEach { commentResponse ->
                            val comment = Comment(
                                name = commentResponse.patient_name ?: "Không rõ",
                                role = "Bệnh nhân",
                                time = formatTime(commentResponse.created_at),
                                rating = commentResponse.star.toFloat(),
                                description = commentResponse.comment_detail,
                                replies = emptyList()
                            )
                            commentsList.add(comment)
                            callApiReply(commentResponse.comment_id, commentsList, comment)
                        }
                        setUpRecycleView(commentsList)
                    } else {
                        Log.d("API", "No comments found, skipping RecyclerView update")
                        setUpRecycleView(emptyList())
                    }
                }, { throwable ->
                    Log.e("API", "API ERROR: $throwable")
                    setUpRecycleView(emptyList())
                })
            )
        }
    }
    private fun callApiReply(commentId: Int, commentsList: MutableList<Comment>, targetComment: Comment) {
        compositeDisposable.add(
            repository.getAllReply(ReplyRequest(commentId)).subscribe({ response ->
                Log.d("API", "API Reply Response for comment $commentId: $response")
                val replies = response.data.map { replyResponse ->
                    Reply(
                        name = replyResponse.user_name ?: "Không rõ",
                        role = when (replyResponse.user_type) {
                            "Patient" -> "Bệnh nhân"
                            "Doctor" -> "Bác sĩ"
                            else -> replyResponse.user_type // Giữ nguyên nếu không xác định
                        },
                        time = "Vừa xong", // Mặc định vì không có created_at
                        description = replyResponse.reply_detail
                    )
                }
                // Cập nhật replies cho comment tương ứng
                val updatedComment = targetComment.copy(replies = replies)
                val index = commentsList.indexOf(targetComment)
                if (index != -1) {
                    commentsList[index] = updatedComment
                }
                // Cập nhật RecyclerView
                setUpRecycleView(commentsList)
            }, { throwable ->
                Log.e("API", "API Reply ERROR for comment $commentId: $throwable")
                // Nếu lỗi, giữ nguyên comment không có reply
                setUpRecycleView(commentsList)
            })
        )
    }
    private fun formatTime(createdAt: String?): String {
        if (createdAt == null) return "Vừa xong"
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatter.parse(createdAt) ?: return "Không rõ"

            val now = Date()
            val diffInMillis = now.time - date.time
            val diffInMinutes = diffInMillis / (1000 * 60)
            val diffInHours = diffInMinutes / 60

            return when {
                diffInMinutes < 60 -> "$diffInMinutes phút trước"
                diffInHours < 24 -> "$diffInHours giờ trước"
                else -> {
                    val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    displayFormatter.format(date)
                }
            }
        } catch (e: Exception) {
            Log.e("TimeFormat", "Error parsing time: $e")
            return "Không rõ"
        }
    }

}