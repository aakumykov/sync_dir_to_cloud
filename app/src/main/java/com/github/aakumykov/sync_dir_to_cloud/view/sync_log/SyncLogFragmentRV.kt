package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentSyncLogRvBinding
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState

class SyncLogFragmentRV : Fragment(R.layout.fragment_sync_log_rv), SyncLogViewHolderClickCallbacks {

    private val menuState = MenuState.noMenu()

    private var _binding: FragmentSyncLogRvBinding? = null
    private val binding get() = _binding!!

    private lateinit var syncLogViewModel: SyncLogViewModel
    private lateinit var pageTitleViewModel: PageTitleViewModel
    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var menuStateViewModel: MenuStateViewModel

    private val taskId: String get() = arguments?.getString(TASK_ID)!!
    private val executionId: String get() = arguments?.getString(EXECUTION_ID)!!

    private lateinit var adapter: LogOfSyncAdapterRV


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareAdapter()
        prepareViewModels()
        startWork(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(menuState)
    }

    private fun prepareAdapter() {
        adapter = LogOfSyncAdapterRV(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    private fun prepareLayout(view: View) {
        _binding = FragmentSyncLogRvBinding.bind(view)
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
            adapter.submitList(list)

            // Костыль против неоторражения списка.
            adapter.notifyDataSetChanged()

            Log.d(TAG, "=========== submitList() ==========")
            list.joinToString(", ") { "${it.text} (${it.subText})" }.also {  s ->
                Log.d(TAG, s)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = SyncLogFragmentRV::class.java.simpleName
        const val TASK_ID: String = "TASK_ID"
        const val EXECUTION_ID: String = "EXECUTION_ID"

        fun create(taskId: String, executionId: String): SyncLogFragmentRV {
            return SyncLogFragmentRV().apply {
                arguments = bundleOf(
                    TASK_ID to taskId,
                    EXECUTION_ID to executionId,
                )
            }
        }
    }


    override fun onSyncLogInfoButtonClicked(logOfSync: LogOfSync) {
        LogItemDetailsDialog.create(logOfSync.toSyncLogDialogInfo()).show(childFragmentManager)
    }

    override fun onSyncingOperationCancelButtonClicked(operationId: String) {
        syncLogViewModel.cancelOperation(operationId)
    }
}