package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel

class SyncLogFragment : Fragment(R.layout.fragment_sync_log) {

    private var _binding: FragmentSyncLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SyncLogViewModel
    private lateinit var pageTitleViewModel: PageTitleViewModel

    private val taskId: String? get() = arguments?.getString(TASK_ID)
    private val executionId: String? get() = arguments?.getString(EXECUTION_ID)

    private lateinit var listAdapter: SyncLogListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareListAdapter()
        prepareViewModels()
        startWork(savedInstanceState)
    }

    private fun prepareListAdapter() {
        listAdapter = SyncLogListAdapter()
        binding.listView.adapter = listAdapter
//        binding.listView.setOnItemClickListener(::onItemClicked)
        binding.listView.isClickable = true
        binding.listView.setOnItemClickListener { parent, view, position, id ->
            showItemInfo(listAdapter.getItem(position))
        }
    }


    private fun onItemClicked(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        showItemInfo(listAdapter.getItem(i))
    }


    private fun showItemInfo(logItem: SyncObjectLogItem) {
        LogItemDetailsDialog.create(logItem).show(childFragmentManager)
    }


    private fun prepareLayout(view: View) {
        _binding = FragmentSyncLogBinding.bind(view)
    }


    private fun startWork(savedInstanceState: Bundle?) {
        if (null == savedInstanceState)
            viewModel.getListLiveData(taskId!!, executionId!!).observe(viewLifecycleOwner, ::onListChanged)
    }

    private fun prepareViewModels() {

        pageTitleViewModel = DaggerViewModelHelper.get(requireActivity(), PageTitleViewModel::class.java).apply {
            setPageTitle(getString(R.string.FRAGMENT_SYNC_LOG_page_title))
        }

        viewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)
    }

    private fun onListChanged(list: List<SyncObjectLogItem>?) {
        list?.also { listAdapter.setList(list) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = SyncLogFragment::class.java.simpleName
        const val TASK_ID: String = "TASK_ID"
        const val EXECUTION_ID: String = "EXECUTION_ID"

        fun create(taskId: String, executionId: String): SyncLogFragment {
            return SyncLogFragment().apply {
                arguments = bundleOf(
                    TASK_ID to taskId,
                    EXECUTION_ID to executionId,
                )
            }
        }
    }
}