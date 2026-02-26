package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogBinding
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.launch

class SyncLogFragment : Fragment(R.layout.fragment_sync_log) {

    private var _binding: FragmentSyncLogBinding? = null
    private val binding get() = _binding!!

    private val adapter = SyncLogAdapter(::onItemClick)
    private lateinit var syncLogViewModel: SyncLogViewModel

    private val taskId: String? get() = arguments?.getString(GlobalKeys.KEY_TASK_ID)
    private val executionId: String? get() = arguments?.getString(GlobalKeys.KEY_EXECUTION_ID)

    private fun onItemClick(logOfSync: LogOfSync) {
        syncLogViewModel.cancelJob(logOfSync.origLogId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSyncLogBinding.bind(view)
        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),  LinearLayout.VERTICAL))

        syncLogViewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)

        lifecycleScope.launch {
            syncLogViewModel.logOfSyncListFlow.collect(::onListChanged)
        }

        if (null == savedInstanceState) {
            if (null != taskId || null != executionId) {
                lifecycleScope.launch {
                    syncLogViewModel.startWorking(taskId!!, executionId!!)
                }
            } else {
                showToast(resources.getString(R.string.SYNC_LOG_error_insufficient_arguments))
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun onListChanged(list: List<LogOfSync>) {
        adapter.submitList(list)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG: String = SyncLogFragment::class.java.simpleName

        fun create(taskId: String, executionId: String): SyncLogFragment {
            return SyncLogFragment().apply {
                arguments = bundleOf(
                    GlobalKeys.KEY_TASK_ID to taskId,
                    GlobalKeys.KEY_EXECUTION_ID to executionId,
                )
            }
        }
    }
}