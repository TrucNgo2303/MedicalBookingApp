package com.example.bookingmedicalapp.ui.patients

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.example.bookingmedicalapp.R
import com.example.bookingmedicalapp.base.BaseDataBindingFragment
import com.example.bookingmedicalapp.base.MainViewModel
import com.example.bookingmedicalapp.databinding.FragmentSearchBinding
import com.example.bookingmedicalapp.source.repository.RemoteRepository
import com.example.bookingmedicalapp.utils.addWithNavFragment
import io.reactivex.disposables.CompositeDisposable
import java.text.Normalizer
import java.util.regex.Pattern

internal class SearchFragment : BaseDataBindingFragment<FragmentSearchBinding, SearchViewModel>() {

    private val repository = RemoteRepository.getInstance()
    private val compositeDisposable = CompositeDisposable()
    private val originalList = mutableListOf<SearchItem>()
    private lateinit var searchAdapter: ArrayAdapter<String>
    private lateinit var mainViewModel: MainViewModel

    data class SearchItem(val name: String, val doctorId: Int?, val specialistId: Int?)

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle()
            }
    }

    override fun layoutResId(): Int = R.layout.fragment_search

    override fun onBackFragmentPressed() {}

    override fun onLeftIconClick() {
        mBinding.root.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        onLeftIconClick()
        setupSearchView()
        callApiSearch()
    }

    private fun callApiSearch() {
        compositeDisposable.add(
            repository.searchResult().subscribe({ response ->
                Log.d("Api", "API Response: $response")

                // Lấy danh sách từ response
                val searchList = response.data.map { SearchItem(it.name, it.doctor_id, it.specialist_id) }
                originalList.clear()
                originalList.addAll(searchList)

                // Khởi tạo adapter và hiển thị gợi ý ban đầu
                searchAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    mutableListOf<String>()
                )
                mBinding.lvSearchResults.adapter = searchAdapter
                showInitialSuggestions()

                // Xử lý sự kiện click vào ListView
                setupListViewClickListener()

            }, { throwable ->
                Log.d("Api", "API Error: ${throwable.message}")
            })
        )
    }

    private fun showInitialSuggestions() {
        // Lọc 2 Bs và 2 Ck ngẫu nhiên
        val bsList = originalList.filter { it.name.startsWith("Bs.") }.shuffled().take(2)
        val ckList = originalList.filter { it.name.startsWith("Ck.") }.shuffled().take(2)
        val suggestionList = (bsList + ckList).shuffled()

        // Hiển thị tiêu đề gợi ý, ẩn tiêu đề kết quả
        mBinding.tvProposeTitle.visibility = View.VISIBLE
        mBinding.tvResultTitle.visibility = View.GONE
        mBinding.lvSearchResults.visibility = View.VISIBLE

        searchAdapter.clear()
        searchAdapter.addAll(suggestionList.map { it.name })
        searchAdapter.notifyDataSetChanged()
    }

    private fun setupListViewClickListener() {
        mBinding.lvSearchResults.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = mBinding.lvSearchResults.adapter.getItem(position) as String
            val selectedSearchItem = originalList.find { it.name == selectedItem }

            selectedSearchItem?.let { item ->
                when {
                    item.doctorId != null -> {
                        mainViewModel.doctor_id_main.value = item.doctorId
                        parentFragmentManager.addWithNavFragment(fragment = DoctorDetailFragment.newInstance())
                    }
                    item.specialistId != null -> {
                        mainViewModel.specialistId.value = item.specialistId
                        parentFragmentManager.addWithNavFragment(fragment = SpecialistDetailFragment.newInstance())
                    }
                }
            }
        }
    }

    private fun setupSearchView() {
        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mBinding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                val normalizedQuery = removeDiacritics(nextText ?: "").lowercase()

                val filteredList = if (normalizedQuery.isEmpty()) {
                    // Hiển thị gợi ý khi query trống
                    val bsList = originalList.filter { it.name.startsWith("Bs.") }.shuffled().take(2)
                    val ckList = originalList.filter { it.name.startsWith("Ck.") }.shuffled().take(2)
                    (bsList + ckList).shuffled()
                } else {
                    // Ẩn tiêu đề gợi ý, hiển thị tiêu đề kết quả
                    mBinding.tvProposeTitle.visibility = View.GONE
                    mBinding.tvResultTitle.visibility = View.VISIBLE
                    originalList.filter {
                        removeDiacritics(it.name).lowercase().contains(normalizedQuery)
                    }
                }

                // Cập nhật adapter và visibility của ListView
                searchAdapter.clear()
                searchAdapter.addAll(filteredList.map { it.name })
                searchAdapter.notifyDataSetChanged()

                mBinding.lvSearchResults.visibility = if (filteredList.isEmpty() && !normalizedQuery.isEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                return false
            }
        })
    }

    // Hàm loại bỏ dấu tiếng Việt
    private fun removeDiacritics(str: String): String {
        val normalized = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(normalized).replaceAll("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}
