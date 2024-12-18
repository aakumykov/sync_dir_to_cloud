package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogRvBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogFragmentRV.Companion

class LogOfSyncFragment : Fragment(R.layout.fragment_sync_log_rv), SyncLogViewHolderClickCallbacks {

    private val taskId: String get() = arguments?.getString(SyncLogFragmentRV.TASK_ID)!!
    private val executionId: String get() = arguments?.getString(SyncLogFragmentRV.EXECUTION_ID)!!

    private var _binding: FragmentSyncLogRvBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogOfSyncViewModel by viewModels()


    override fun onSyncLogInfoButtonClicked(logOfSync: LogOfSync) {
        TODO("Not yet implemented")
    }

    override fun onSyncingOperationCancelButtonClicked(operationId: String) {
        TODO("Not yet implemented")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSyncLogRvBinding.bind(view)

        val adapter = LogOfSyncAdapterRV(this)

        if (null == savedInstanceState) {
            viewModel.startWork(taskId, executionId)
        }

        viewModel.logOfSync.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = SyncLogFragmentRV::class.java.simpleName

        const val TASK_ID: String = "TASK_ID"
        const val EXECUTION_ID: String = "EXECUTION_ID"

        fun create(taskId: String, executionId: String): LogOfSyncFragment {
            return LogOfSyncFragment().apply {
                arguments = bundleOf(
                    TASK_ID to taskId,
                    EXECUTION_ID to executionId,
                )
            }
        }
    }
}