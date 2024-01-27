package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.WorkManagerConfig
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.utils.isAndroidTiramisuOrLater
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.ItemClickCallback
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.TaskListAdapter
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.constructPermissionsRequest

class TaskListFragment : Fragment(R.layout.fragment_task_list), ItemClickCallback {

    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }

    // View binding
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // RecyclerView adapter
    private var taskListAdapter: TaskListAdapter? = null

    // ViewModels
    private val taskListViewModel: TaskListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareAdapter()
        prepareRecyclerView()
        prepareButtons()
        prepareViewModels()
        setPageTitle()
    }

    override fun onDestroyView() {
        taskListAdapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onProbeRunClicked(taskId: String) {

        lifecycleScope.launch (Dispatchers.IO) {

            WorkManager.getInstance(requireContext())
                .beginUniqueWork(
                    probeTaskId(taskId),
                    ExistingWorkPolicy.KEEP,
                    oneTimeWorkRequest(taskId)
                )
                .enqueue()
        }
    }

    override fun onProbeRunLongClicked(taskId: String) {
        WorkManager.getInstance(requireContext())
                .cancelUniqueWork(probeTaskId(taskId))
                .state.observe(viewLifecycleOwner) { operationState ->

                    when (operationState) {
                        is Operation.State.SUCCESS -> showToast("Задача отменена")
                        is Operation.State.FAILURE -> showToast("Ошибка отмены задачи")
                        else -> {}
                    }

                    App.getAppComponent().also { appComponent ->
                        lifecycleScope.launch {
                            val task = appComponent.getSyncTaskReader().getSyncTask(taskId)
                            appComponent.getSyncTaskNotificator().hideNotification(taskId, task.notificationId)
                        }
                    }
                }
    }

    private fun oneTimeWorkRequest(taskId: String): OneTimeWorkRequest {
        return OneTimeWorkRequest.Builder(
            SyncTaskWorker::class.java
        )
            .addTag(SyncTask.TAG)
            .setInputData(SyncTaskWorker.dataWithTaskId(taskId))
            .build()
    }

    private fun probeTaskId(taskId: String) = WorkManagerConfig.Companion.PROBE_WORK_ID_PREFIX + taskId


    override fun onTaskEditClicked(taskId: String) {
        navigationViewModel.navigateTo(NavTarget.Edit(taskId))
    }

    override fun onTaskRunClicked(taskId: String) {
        taskListViewModel.startStopTask(taskId)
    }

    override fun onTaskDeleteClicked(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskInfoClicked(taskId: String) {
        navigationViewModel.navigateTo(NavTarget.TaskInfo(taskId))
    }

    override fun onTaskEnableSwitchClicked(taskId: String) {
        taskListViewModel.changeTaskEnabled(taskId)
    }


    private fun prepareLayout(view: View) {
        _binding = FragmentTaskListBinding.bind(view)
    }


    private fun prepareAdapter() {
        taskListAdapter = TaskListAdapter(this)
    }


    private fun prepareRecyclerView() {
        binding.recyclerView.adapter = taskListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    private fun prepareViewModels() {
        lifecycleScope.launch {
            taskListViewModel.getTaskList().observe(viewLifecycleOwner) { onListChanged(it) }
        }
    }


    private fun setPageTitle() {
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_LIST_title))
    }


    private fun prepareButtons() {
        binding.addButton.setOnClickListener {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (isAndroidTiramisuOrLater()) {
            constructPermissionsRequest(
                android.Manifest.permission.POST_NOTIFICATIONS,
                requiresPermission = { goToAddNewTask() },
                onShowRationale = {
                    AlertDialog.Builder(requireContext()).apply{
                        setTitle("Показ уведомлений")
                        setMessage("Разрешить программе отображать ход синхронизации?")
                        setPositiveButton("Конечно!") { _, _ -> it.proceed() }
                        setNegativeButton("Ни-за-что!") { _, _ -> it.cancel() }
                    }
                }
            ).launch()
        }
        else {
            goToAddNewTask()
        }
    }

    private fun goToAddNewTask() {
        navigationViewModel.navigateTo(NavTarget.Add)
    }


    private fun onListChanged(list: List<SyncTask>) {
        taskListAdapter?.setList(list)
    }

}

