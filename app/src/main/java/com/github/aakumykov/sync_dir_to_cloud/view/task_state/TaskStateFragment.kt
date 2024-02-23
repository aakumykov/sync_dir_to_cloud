package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskStateBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

class TaskStateFragment : Fragment(R.layout.fragment_task_state) {

    private var _binding: FragmentTaskStateBinding? = null
    private val binding get() = _binding!!

    // TODO: внудрять ViewModel-и Dagger-ом?
    private lateinit var taskStateViewModel: TaskStateViewModel // эту получаю от Dagger-а
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    private lateinit var listAdapter: ListViewAdapter<SyncObject>
    private val syncObjectList: MutableList<SyncObject> = mutableListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareView(view)
        prepareListAdapter()
        prepareViewModels()
        processArguments()
    }

    private fun prepareViewModels() {
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_INFO_title))
        taskStateViewModel = DaggerViewModelHelper.get(this, TaskStateViewModel::class.java)

    }

    private fun processArguments() {

        val taskId: String? = arguments?.getString(KEY_TASK_ID)

        if (null == taskId) {
            showToast(R.string.there_is_no_task_id)
            navigationViewModel.navigateBack()
            return
        }

        lifecycleScope.launch {
            taskStateViewModel.getSyncObjectList(taskId).observe(viewLifecycleOwner) { list ->
                syncObjectList.clear()
                syncObjectList.addAll(list)
                listAdapter.notifyDataSetChanged()
            }

            taskStateViewModel.getSyncTask(taskId).observe(viewLifecycleOwner, ::displaySyncTask)
        }
    }

    private fun prepareView(view: View) {
        _binding = FragmentTaskStateBinding.bind(view)
    }

    private fun prepareListAdapter() {

        listAdapter = ListViewAdapter(requireContext(), R.layout.sync_object_list_item, R.id.title, syncObjectList) {
                syncObject -> SyncStateItem(syncObject).title
        }

        binding.listView.adapter = listAdapter

        binding.listView.setOnItemClickListener { _, _, position, _ -> showItemInfo(syncObjectList[position]) }
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
                append(syncObject.modificationState.name)
                append("\n")

                append("Время синхронизации: ")
                append(dateStringOrNever(syncObject.syncDate))
                append("\n")

                if (ExecutionState.ERROR == syncObject.executionState) {
                    append("Ошибка синхронизации: ")
                    append(syncObject.executionError)
                    append("\n")
                }
            })

            setPositiveButton(R.string.DIALOG_BUTTON_close) { _, _ ->  }
        }
            .create().show()
    }

    private fun displaySyncTask(syncTask: SyncTask?) {
        if (null == syncTask)
            return

        binding.titleView.text = "${syncTask.sourcePath} --> ${syncTask.targetPath}"
        binding.idView.text = "(${syncTask.id})"

        displaySchedulingState(syncTask)
        displayExecutionState(syncTask)
        displayLastRunState(syncTask)
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
        binding.executionStateView.text = when (syncTask.executionState) {
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