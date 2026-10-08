package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.GlobalKeys.KEY_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogBinding
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.launch

class SyncLogFragment : Fragment(R.layout.fragment_sync_log) {

    private var _binding: FragmentSyncLogBinding? = null
    private val binding get() = _binding!!

    private val adapter = SyncLogAdapter(::onItemClicked, ::onOperationCancelClicked)
    private lateinit var syncLogViewModel: SyncLogViewModel

    private val taskId: String? get() = arguments?.getString(KEY_TASK_ID)
    private val executionId: String? get() = arguments?.getString(KEY_EXECUTION_ID)


    private fun onItemClicked(origLogId: String, logItemAbout: LogItemAbout) {
        OperationDetailsDialog.create(origLogId, logItemAbout)
            .show(parentFragmentManager, OperationDetailsDialog.TAG)
    }

    private fun onOperationCancelClicked(origLogId: String) {
        syncLogViewModel.cancelJob(origLogId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSyncLogBinding.bind(view)
        binding.recyclerView.adapter = this.adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(),  LinearLayout.VERTICAL))
        binding.recyclerView.itemAnimator = null

        syncLogViewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)

        lifecycleScope.launch {
            syncLogViewModel
                .logOfSyncListFlow
                .collect(::onListChanged)
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
        adapter.submitList(list) {
            binding.recyclerView.scrollToPosition(adapter.itemCount-1)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        val TAG: String = SyncLogFragment::class.java.simpleName

        fun create(taskId: String?, executionId: String?): SyncLogFragment {
            return SyncLogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_TASK_ID, taskId)
                    putString(KEY_EXECUTION_ID, executionId)
                }
            }
        }

        fun create(bundle: Bundle?): SyncLogFragment {
            val taskId = bundle?.getString(KEY_TASK_ID)
            val executionId = bundle?.getString(KEY_EXECUTION_ID)
            return create(taskId, executionId)
        }
    }
}