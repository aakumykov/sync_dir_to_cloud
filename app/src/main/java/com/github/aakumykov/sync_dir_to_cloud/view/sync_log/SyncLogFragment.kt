package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogBinding
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState

class SyncLogFragment : Fragment(R.layout.fragment_sync_log), SyncLogViewHolderClickCallbacks {

    private val menuState = MenuState.noMenu()

    private var _binding: FragmentSyncLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var syncLogViewModel: SyncLogViewModel
    private lateinit var pageTitleViewModel: PageTitleViewModel
    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var menuStateViewModel: MenuStateViewModel

    private val taskId: String get() = arguments?.getString(TASK_ID)!!
    private val executionId: String get() = arguments?.getString(EXECUTION_ID)!!

//    private lateinit var listAdapter: SyncLogListAdapter
    private lateinit var adapter: LogOfSyncAdapterRV


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareRecyclerView()
        prepareViewModels()
        startWork(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(menuState)
    }

    private fun prepareRecyclerView() {
        adapter = LogOfSyncAdapterRV(this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    private fun onItemClicked(logOfSyncItem: LogOfSync) {
        LogItemDetailsDialog.create(logOfSyncItem.toSyncLogDialogInfo()).show(childFragmentManager)
//        syncLogViewModel.cancelJob("qwerty")
    }


    private fun prepareLayout(view: View) {
        _binding = FragmentSyncLogBinding.bind(view)
    }


    private fun startWork(savedInstanceState: Bundle?) {
        if (null == savedInstanceState) {
            syncLogViewModel.startWorking(taskId,executionId)
            syncLogViewModel.logOfSync.observe(viewLifecycleOwner, ::onLogOfSyncChanged)
//            syncLogViewModel.getListLiveData(taskId!!, executionId!!)
//                .observe(viewLifecycleOwner, ::onListChanged)
        }
    }

    private fun prepareViewModels() {

        pageTitleViewModel = DaggerViewModelHelper.get(requireActivity(), PageTitleViewModel::class.java).apply {
            setPageTitle(getString(R.string.FRAGMENT_SYNC_LOG_page_title))
        }

        navigationViewModel = DaggerViewModelHelper.get(requireActivity(), NavigationViewModel::class.java)

        menuStateViewModel = DaggerViewModelHelper.get(requireActivity(), MenuStateViewModel::class.java)

        syncLogViewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)
    }

//    private fun onListChanged(list: List<SyncObjectLogItem>?) {
//        list?.also { listAdapter.setList(list) }
//    }

    private fun onLogOfSyncChanged(list: List<LogOfSync>?) {
        list?.also {
            Log.d(TAG, "-------------------- List to submit -------------------")
            list.forEach { Log.d(TAG, it.toString()) }
            adapter.submitList(list)
        }
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


    override fun onSyncLogInfoButtonClicked(logOfSync: LogOfSync) {

    }

    override fun onSyncingOperationCancelButtonClicked(operationId: String) {
        syncLogViewModel.cancelOperation(operationId)
    }
}