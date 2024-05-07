package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequest
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.WorkManagerConfig
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.openAppProperties
import com.github.aakumykov.sync_dir_to_cloud.utils.isAndroidTiramisuOrLater
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.ItemClickCallback
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.TaskListAdapter
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import permissions.dispatcher.ktx.constructPermissionsRequest

class TaskListFragment : Fragment(R.layout.fragment_task_list),
    ItemClickCallback,
    PopupMenu.OnMenuItemClickListener
{
    private val menuItems = arrayOf(
        CustomMenuItem(
            id = R.id.actionAppProperties,
            title = R.string.MENU_ITEM_app_properties,
            icon = R.drawable.ic_app_properties,
            action = { activity?.openAppProperties() }
        ),
        CustomMenuItem(
            id = R.id.actionManageExternalStorage,
            title = R.string.MENU_ITEM_manage_external_storage,
            icon = R.drawable.ic_storage,
            action = { activity?.also { StorageAccessHelper.openStorageAccessSettings(this) } }
        )
    )

    private var clickedTask: SyncTask? = null

    // View binding
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // RecyclerView adapter
    private var taskListAdapter: TaskListAdapter? = null

    // ViewModels
    private lateinit var taskListViewModel: TaskListViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()
    private val menuStateViewModel: MenuStateViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareAdapter()
        prepareRecyclerView()
        prepareButtons()
        prepareViewModels()
        setPageTitle()
    }

    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(MenuState(*menuItems))
    }

    override fun onDestroyView() {
        taskListAdapter = null
        _binding = null
        super.onDestroyView()
    }


    override fun onProbeRunClicked(taskId: String) {

        lifecycleScope.launch (Dispatchers.IO) {

            /*WorkManager.getInstance(requireContext())
                .beginUniqueWork(
                    probeTaskId(taskId),
                    ExistingWorkPolicy.KEEP,
                    oneTimeWorkRequest(taskId)
                )
                .enqueue()*/

            val currentState = App.getAppComponent().getSyncTaskStateDAO().getState(taskId)

            App.getAppComponent().getSyncTaskStateDAO().setStateSuspend(taskId, when(currentState) {
                SyncTask.State.IDLE -> SyncTask.State.WRITING_TARGET
                else -> SyncTask.State.IDLE
            })
        }
    }

/*    override fun onProbeRunLongClicked(taskId: String) {
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
*/

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

    override fun onTaskMoreButtonClicked(itemView: View, anchorView: View, syncTask: SyncTask) {

        clickedTask = syncTask

        PopupMenu(requireContext(), anchorView).apply {
            menuInflater.inflate(R.menu.task_list_item_popup_menu, menu)
            setOnMenuItemClickListener(this@TaskListFragment)
            setOnDismissListener { clickedTask = null }
        }.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.actionEnablingToggle -> {
                taskListViewModel.changeTaskEnabled(clickedTask!!.id)
                true
            }
            R.id.actionDelete -> {
                showDeleteTaskDialog()
                true
            }
            else -> false
        }
    }

    private fun showDeleteTaskDialog() {
        clickedTask?.also { task ->
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.DELETE_DIALOG_title)
                .setMessage(task.summary())
                .setPositiveButton(R.string.DIALOG_BUTTON_yes, DialogInterface.OnClickListener { dialog, which ->
                    taskListViewModel.deleteTask(task)
                })
                .setNegativeButton(R.string.DIALOG_BUTTON_no, DialogInterface.OnClickListener { _, _ -> })
                .create()
                .show()
        }
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
        taskListViewModel = DaggerViewModelHelper.get(this, TaskListViewModel::class.java)

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


    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }
}

