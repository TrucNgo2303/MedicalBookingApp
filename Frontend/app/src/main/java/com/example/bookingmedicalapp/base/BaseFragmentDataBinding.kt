package com.example.bookingmedicalapp.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

internal abstract class BaseDataBindingFragment<BD : ViewDataBinding, VM> :
    Fragment() where VM : BaseViewModel {

    @LayoutRes
    abstract fun layoutResId(): Int
    abstract fun onBackFragmentPressed()
    abstract fun onLeftIconClick()
    abstract fun initialize()

    lateinit var mBinding: BD
    lateinit var mViewModel: VM
    lateinit var mContext: Context
    lateinit var mActivity: Activity
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(this)[getViewModelClass()]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mActivity = requireActivity()
        mContext = requireContext()
        mBinding = DataBindingUtil.inflate(inflater, layoutResId(), container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            // Safe to use context
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        onBackFragmentPressed()
                    }
                })
        }
        initialize()
    }

    private fun getViewModelClass(): Class<VM> {
        @Suppress("UNCHECKED_CAST")
        return (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>
    }

//    fun getMainActivity(): SDKMainActivity? {
//        return (activity as? SDKMainActivity)
//    }

    fun getMainViewModel(): MainViewModel {
        return mainViewModel
    }

    fun getSupportFragmentManager(): FragmentManager = requireActivity().supportFragmentManager

//    fun showLoading() {
//        LoadingOverlayManager.show(requireContext())
//    }
//
//    fun hideLoading() {
//        LoadingOverlayManager.hide()
//    }
}