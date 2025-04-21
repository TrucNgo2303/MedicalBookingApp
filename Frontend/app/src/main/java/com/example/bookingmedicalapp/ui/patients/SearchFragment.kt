package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.databinding.FragmentSearchBinding
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import io.reactivex.disposables.CompositeDisposable
import java.text.Normalizer
import java.util.regex.Pattern

internal class SearchFragment : BaseDataBindingFragment<FragmentSearchBinding, SearchViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_search

    override fun onBackFragmentPressed() {

    }

    override fun onLeftIconClick() {
        mBinding.root.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        onLeftIconClick()
        callApiSearch()
    }

    private fun callApiSearch() {
        compositeDisposable.add(
            repository.searchResult().subscribe({ response ->
                Log.d("Api", "API Response: $response")

                // Lấy danh sách full_name từ response
                val searchList = response.data.map { it.name }

                // Cập nhật ListView với dữ liệu mới
                updateSearchResults(searchList)

            }, { throwable ->
                Log.d("Api", "API Error: ${throwable.message}")
            })
        )
    }

    // Hàm loại bỏ dấu tiếng Việt
    private fun removeDiacritics(str: String): String {
        val normalized = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("")
    }

    // Lưu danh sách gốc để khôi phục khi người dùng xóa tìm kiếm
    private val originalList = mutableListOf<String>()

    private fun updateSearchResults(searchList: List<String>) {
        // Lưu danh sách gốc khi khởi tạo lần đầu
        if (originalList.isEmpty()) {
            originalList.addAll(searchList)
        }

        val searchAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1,
            searchList.toMutableList()
        )

        mBinding.lvSearchResults.adapter = searchAdapter

        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mBinding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                val normalizedQuery = removeDiacritics(nextText ?: "").lowercase()

                val filteredList = if (normalizedQuery.isEmpty()) {
                    originalList // Nếu ô tìm kiếm trống, hiển thị danh sách gốc
                } else {
                    originalList.filter {
                        removeDiacritics(it).lowercase().contains(normalizedQuery)
                    }
                }

                // Cập nhật Adapter với danh sách mới
                searchAdapter.clear()
                searchAdapter.addAll(filteredList)
                searchAdapter.notifyDataSetChanged()

                return false
            }
        })
    }

}