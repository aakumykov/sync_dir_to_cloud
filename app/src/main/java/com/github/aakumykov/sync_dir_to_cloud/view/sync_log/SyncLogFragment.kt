package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.compose.SyncLogScreen
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SyncLogFragment : Fragment(R.layout.fragment_sync_log) {

    private var _binding: FragmentSyncLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SyncLogViewModel
    private lateinit var pageTitleViewModel: PageTitleViewModel

    private val taskId: String? get() = arguments?.getString(TASK_ID)
    private val executionId: String? get() = arguments?.getString(EXECUTION_ID)

    private val listState = mutableStateListOf<SyncObjectLogItem>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView(view)
        prepareViewModels()
        startWork(savedInstanceState)
    }


    private fun prepareView(view: View) {

        _binding = FragmentSyncLogBinding.bind(view)

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SyncLogScreen(
                    taskId = taskId ?: "null",
                    executionId = executionId ?: "null",
                    logInfoList = listState
                )
            }
        }
    }


    private fun startWork(savedInstanceState: Bundle?) {
        if (null == savedInstanceState)
            viewModel.startWork(taskId!!, executionId!!)
    }

    private fun prepareViewModels() {

        pageTitleViewModel = DaggerViewModelHelper.get(requireActivity(), PageTitleViewModel::class.java).apply {
            setPageTitle(getString(R.string.FRAGMENT_SYNC_LOG_page_title))
        }

        viewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)

        viewModel.syncObjectInfoList.observe(viewLifecycleOwner, ::onListChanged)
    }

    private fun onListChanged(syncObjectLogItemList: List<SyncObjectLogItem>?) {
        syncObjectLogItemList?.also { listState.addAll(syncObjectLogItemList) }
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