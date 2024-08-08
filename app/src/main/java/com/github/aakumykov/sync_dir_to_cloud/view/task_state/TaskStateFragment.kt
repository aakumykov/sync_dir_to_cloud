package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.Constants.DEFAULT_BACK_STACK_NAME
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskStateBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.progress_info_holder.ProgressInfoHolder
import com.github.aakumykov.sync_dir_to_cloud.sync_task_logger.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.ListViewAdapter
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditFragment
import kotlinx.coroutines.launch

class TaskStateFragment : Fragment(R.layout.fragment_task_state) {

    private val menuItems = arrayOf(
        CustomMenuItem(
            id = R.id.actionStartStopTask,
            title = R.string.MENU_ITEM_action_start_stop_task,
            icon = R.drawable.ic_task_start_toolbar,
            action = { taskStateViewModel.startStopTask(currentTaskId) }),
        CustomMenuItem(
            id = R.id.actionEditTask,
            title = R.string.MENU_ITEM_action_edit_task,
            icon = R.drawable.ic_task_edit_toolbar,
            action = ::onTaskEditClicked
        )
    )

    private val menuState = MenuState(*menuItems)

    private var _binding: FragmentTaskStateBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskStateViewModel: TaskStateViewModel
    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var pageTitleViewModel: PageTitleViewModel
    private lateinit var menuStateViewModel: MenuStateViewModel

    private lateinit var taskLogAdapter: ListHoldingListAdapter<TaskLogEntry, TaskLogViewHolder>

    private lateinit var currentTaskId: String

    private lateinit var progressInfoHolder: ProgressInfoHolder


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressInfoHolder = App.getAppComponent().getProgressInfoHolder()

        prepareView(view)
        prepareListAdapter()
        prepareViewModels()
        processArguments()

        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_INFO_title))
    }

    override fun onResume() {
        super.onResume()
        menuStateViewModel.sendMenuState(menuState)
    }

    private fun prepareViewModels() {
        // Местная ViewModel
        taskStateViewModel = DaggerViewModelHelper.get(this, TaskStateViewModel::class.java)

        // ViewModel-и уровня приложения
        pageTitleViewModel = DaggerViewModelHelper.get(requireActivity(), PageTitleViewModel::class.java)
        navigationViewModel = DaggerViewModelHelper.get(requireActivity(), NavigationViewModel::class.java)
        menuStateViewModel = DaggerViewModelHelper.get(requireActivity(), MenuStateViewModel::class.java)
    }

    private fun processArguments() {

        arguments?.getString(KEY_TASK_ID)?.also { taskId ->

            currentTaskId = taskId

            lifecycleScope.launch {
                taskStateViewModel.getSyncTask(currentTaskId).observe(viewLifecycleOwner, ::onTaskChanged)
                taskStateViewModel.getTaskLogsLiveData(currentTaskId).observe(viewLifecycleOwner, ::onTaskLogListChanged)
            }

        } ?: {
            showToast(R.string.there_is_no_task_id)
            navigationViewModel.navigateBack()
        }
    }

    private fun onTaskLogListChanged(taskLogEntries: List<TaskLogEntry>?) {
        taskLogEntries?.also { taskLogAdapter.setList(it) }
    }

    private fun prepareView(view: View) {
        _binding = FragmentTaskStateBinding.bind(view)
    }

    private fun prepareListAdapter() {
        taskLogAdapter = TaskLogAdapter()
        binding.listView.adapter = taskLogAdapter
        binding.listView.setOnItemClickListener(::onTaskLogItemClicked)
    }


    private fun onTaskLogItemClicked(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        showToast(taskLogAdapter.getItem(i).taskId)
    }

    private fun showItemInfo(syncObject: SyncObject) {
        AlertDialog.Builder(requireContext()).apply {

            setTitle(R.string.sync_object_info_dialog_title)

            setMessage(StringBuilder().apply {

                append("Имя: ")
                append(syncObject.name)
                append("\n")

                append("Время изменения: ")
                append(CurrentDateTime.format(syncObject.mTime))
                append("\n")

                append("Тип изменения: ")
                append(syncObject.stateInSource.name)
                append("\n")

                append("Время синхронизации: ")
                append(dateStringOrNever(syncObject.syncDate))
                append("\n")

                if (ExecutionState.ERROR == syncObject.syncState) {
                    append("Ошибка синхронизации: ")
                    append(syncObject.syncError)
                    append("\n")
                }
            })

            setPositiveButton(R.string.DIALOG_BUTTON_close) { _, _ ->  }
        }
            .create().show()
    }

    private fun onTaskChanged(syncTask: SyncTask?) {

        if (null == syncTask)
            return

        binding.titleView.text = "${syncTask.sourcePath} --> ${syncTask.targetPath}"
        binding.idView.text = "(${syncTask.id})"

        changeToolbarButtons(syncTask)

        displaySchedulingState(syncTask)
        displayExecutionState(syncTask)
        displayLastRunState(syncTask)
    }

    private fun changeToolbarButtons(syncTask: SyncTask) {
        menuState.updateIcon(R.id.actionStartStopTask, when(syncTask.executionState){
            ExecutionState.RUNNING -> R.drawable.ic_task_stop_toolbar
            else -> R.drawable.ic_task_start_toolbar
        }).also {
            menuStateViewModel.sendMenuState(it)
        }
    }

    private fun displayLastRunState(syncTask: SyncTask) {
        val lastStartDateString = syncTask.lastStart?.let { CurrentDateTime.format(it) } ?: getString(R.string.never)

        val lastFinishDateString = syncTask.lastFinish?.let {
            if (0L == it) getString(R.string.last_run_unknown_yet)
            else CurrentDateTime.format(it)
        } ?: getString(R.string.never)

        binding.lastStartInfo.text = getString(R.string.last_start_info, lastStartDateString)
        binding.lastFinishInfo.text = getString(R.string.last_finish_info, lastFinishDateString)
    }

    private fun displaySchedulingState(syncTask: SyncTask) {
        binding.schedulingStateView.text =
            if (syncTask.isEnabled) {
                when(syncTask.schedulingState) {
                    ExecutionState.NEVER -> detailedSchedulingState(syncTask)
                    ExecutionState.SUCCESS -> detailedSchedulingState(syncTask)
                    ExecutionState.RUNNING -> getString(R.string.SCHEDULING_STATE_scheduling_now)
                    ExecutionState.ERROR -> getString(R.string.SCHEDULING_STATE_scheduling_error, syncTask.schedulingError)
                }
            }
            else {
                getString(R.string.SCHEDULING_STATE_disabled)
            }
    }

    private fun detailedSchedulingState(syncTask: SyncTask): String {
        return when (syncTask.intervalHours) {
            0 -> getString(
                R.string.SCHEDULING_STATE_scheduled_short_form,
                syncTask.intervalMinutes,
                resources.getQuantityString(R.plurals.minutes, syncTask.intervalMinutes))

            else ->getString(
                R.string.SCHEDULING_STATE_scheduled_long_form,
                syncTask.intervalHours,
                syncTask.intervalMinutes,
                resources.getQuantityString(R.plurals.hours, syncTask.intervalHours),
                resources.getQuantityString(R.plurals.minutes, syncTask.intervalMinutes))
        }
    }

    private fun displayExecutionState(syncTask: SyncTask) {
        binding.syncStateView.text = when (syncTask.executionState) {
            ExecutionState.NEVER -> getString(R.string.EXECUTION_STATE_idle)
            ExecutionState.RUNNING -> getString(R.string.EXECUTION_STATE_running)
            ExecutionState.SUCCESS -> getString(R.string.EXECUTION_STATE_success)
            ExecutionState.ERROR -> getString(
                R.string.EXECUTION_STATE_error,
                syncTask.executionError
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dateStringOrNever(timestamp: Long): String {
        return if (0L == timestamp) getString(R.string.never)
        else CurrentDateTime.format(timestamp)
    }

    private fun onTaskEditClicked() {
        TaskEditFragment.create(currentTaskId).also {
            parentFragmentManager.beginTransaction()
                .addToBackStack(DEFAULT_BACK_STACK_NAME)
                .replace(R.id.fragmentContainerView, it)
                .commit()
        }
    }

    companion object {

        const val KEY_TASK_ID = "TASK_ID"

        fun create(taskId: String?): TaskStateFragment {
            return TaskStateFragment().apply {
                arguments = Bundle().apply { putString(KEY_TASK_ID, taskId) }
            }
        }

        fun create(intent: Intent): TaskStateFragment {
            return create(intent.getStringExtra(KEY_TASK_ID))
        }
    }
}

class TaskLogAdapter : ListHoldingListAdapter<TaskLogEntry, TaskLogViewHolder>(R.layout.task_log_item) {
    override fun createViewHolder(): ViewHolder<TaskLogEntry> {
        return TaskLogViewHolder()
    }
}

class TaskLogViewHolder : ListHoldingListAdapter.ViewHolder<TaskLogEntry>() {

    private lateinit var titleView: TextView

    override fun fill(item: TaskLogEntry, isSelected: Boolean) {
        titleView.text = CurrentDateTime.format(item.timestamp)
    }

    override fun init(itemView: View) {
        titleView = itemView.findViewById(R.id.titleView)
    }
}