package com.example.bookingmedicalapp.ui.bottomNav

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.adapter.ProfileAdapter
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentProfileBinding
import com.example.bookingmedicalapp.model.ProfileItem
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.ui.LoadingActivity
import com.example.bookingmedicalapp.ui.signupsignin.SignInFragment
import com.example.bookingmedicalapp.utils.TokenAction
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable


internal class ProfileFragment : BaseDataBindingFragment<FragmentProfileBinding, ProfileViewModel>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileAdapter
    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()


    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle()
                }
            }

    override fun layoutResId(): Int = R.layout.fragment_profile

    override fun onBackFragmentPressed() {
        TODO("Not yet implemented")
    }

    override fun onLeftIconClick() {
        mBinding.imvBackIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        callApi()

        recyclerView = mBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val profileItems = listOf(
            ProfileItem(R.drawable.user_edit_profile, "Chỉnh sửa hồ sơ", R.drawable.arrow_right),
            ProfileItem(R.drawable.user_favourite, "Yêu thích", R.drawable.arrow_right),
            ProfileItem(R.drawable.user_notification, "Thông báo", R.drawable.arrow_right),
            ProfileItem(R.drawable.setting, "Cài đặt", R.drawable.arrow_right),
            ProfileItem(R.drawable.user_help, "Trợ giúp & Hỗ trợ", R.drawable.arrow_right),
            ProfileItem(R.drawable.user_security, "Điều khoản & Điều kiện", R.drawable.arrow_right),
            ProfileItem(R.drawable.log_out, "Đăng xuất", R.drawable.empty),
            )
        adapter = ProfileAdapter(profileItems) { item ->
            when (item.title) {
                "Chỉnh sửa hồ sơ" -> Toast.makeText(context, "Edit Profile Clicked", Toast.LENGTH_SHORT).show()
                "Đăng xuất" -> {
                    val sharedPref = TokenAction(requireContext())
                    sharedPref.clearToken()
                    // Xóa toàn bộ back stack
                    parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    val intent = Intent(requireContext(), LoadingActivity::class.java) // Thay bằng Activity chứa SignInFragment
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                    //parentFragmentManager.addWithNavFragment(fragment = SignInFragment.newInstance())
                }
                else -> Toast.makeText(context, "${item.title} Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

    }
    private fun callApi(){
        compositeDisposable.add(
            repository.getHomePatientInfo().subscribe({ response ->
                Log.d("API", "API Response: $response")
                mBinding.tvFullName.text = response.data.full_name
                if(response.data.avatar != null) {
                    Glide.with(mBinding.root.context)
                        .load(response.data.avatar)
                        .into(mBinding.imvAvatar)
                }

            },{ throwable ->
                Log.e("API", "API ERROR: $throwable")

            })
        )
    }

}
