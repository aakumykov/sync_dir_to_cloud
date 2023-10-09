package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.utils.TextMessage
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class TaskEditFragment : Fragment() {

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private val taskEditViewModel: TaskEditViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        prepareLayout(inflater, container)
        prepareButtons()
        prepareViewModels()
        return binding.root
    }


    private fun prepareLayout(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)
    }


    private fun prepareButtons() {

        binding.periodHoursView.setOnClickListener { pickExecutionPeriod() }
        binding.periodMinutesView.setOnClickListener { pickExecutionPeriod() }

        binding.saveButton.setOnClickListener {
            taskEditViewModel.createOrSaveSyncTask(
                binding.sourcePathInput.text.toString(),
                binding.targetPathInput.text.toString()
            )
        }

        binding.cancelButton.setOnClickListener {
            navigationViewModel.navigateTo(NavTarget.Back)
        }
    }

    private fun pickExecutionPeriod() {

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(10)
            .setTitleText(R.string.sync_task_regulatiry_picker_title)
            .build()

        picker.addOnPositiveButtonClickListener {

        }

        picker.showNow(childFragmentManager, "")
    }


    private fun prepareViewModels() {
        taskEditViewModel.getCurrentTask().observe(viewLifecycleOwner, this::onCurrentTaskChanged)
        taskEditViewModel.getOpState().observe(viewLifecycleOwner, this::onOpStateChanged)
    }


    private fun onOpStateChanged(opState: OpState) {
        when (opState) {
            is OpState.Idle -> showIdleOpState()
            is OpState.Busy -> showBusyOpState(opState)
            is OpState.Success -> finishWork(opState)
            is OpState.Error -> showErrorOpState(opState)
        }
    }


    private fun finishWork(successOpState: OpState.Success) {
        Toast.makeText(requireContext(), successOpState.textMessage.get(requireContext()), Toast.LENGTH_SHORT).show()
        navigationViewModel.navigateBack()
    }


    private fun onCurrentTaskChanged(syncTask: SyncTask) {
        fillForm(syncTask)
    }


    private fun fillForm(syncTask: SyncTask) {
        binding.sourcePathInput.setText(syncTask.sourcePath)
        binding.targetPathInput.setText(syncTask.targetPath)

        fillPeriodView(syncTask)
    }

    private fun fillPeriodView(syncTask: SyncTask) {
        val calendar: Calendar = Calendar.getInstance().apply {
            time = Date(syncTask.executionPeriod)
        }
        binding.periodHoursView.setText(calendar.get(Calendar.HOUR).toString())
        binding.periodMinutesView.setText(calendar.get(Calendar.MINUTE).toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId: String? = arguments?.getString(TASK_ID)

        pageTitleViewModel.setPageTitle(getString(
            when(taskId) {
                null -> R.string.FRAGMENT_TASK_EDIT_creation_title
                else -> R.string.FRAGMENT_TASK_EDIT_edition_title
            }
        ))

        // TODO: преобразовать в один метод "prepare"
        if (null == taskId)
            taskEditViewModel.prepareForNewTask()
        else
            taskEditViewModel.loadTask(taskId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showIdleOpState() {
        hideProgressBar()
        hideProgressMessage()
        enableForm()
        hideErrorMessage()
    }

    private fun showBusyOpState(opState: OpState.Busy) {
        showProgressBar()
        showProgressMessage(opState.textMessage)
        disableForm()
        hideErrorMessage()
    }

    private fun showErrorOpState(opState: OpState.Error) {
        hideProgressBar()
        hideProgressMessage()
        enableForm()
        showErrorMessage(opState.errorMessage)
    }


    private fun showProgressMessage(textMessage: TextMessage) {
        binding.progressMessage.text = textMessage.get(requireContext())
        binding.progressMessage.visibility = View.VISIBLE
    }

    private fun hideProgressMessage() {
        binding.progressMessage.text = ""
        binding.progressMessage.visibility = View.GONE
    }


    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }


    private fun enableForm() {
        binding.sourcePathInput.isEnabled = true
        binding.targetPathInput.isEnabled = true

        binding.selectSourcePathButton.isEnabled = true
        binding.selectTargetPathButton.isEnabled = true

        binding.saveButton.isEnabled = true
    }

    private fun disableForm() {
        binding.sourcePathInput.isEnabled = false
        binding.targetPathInput.isEnabled = false

        binding.selectSourcePathButton.isEnabled = false
        binding.selectTargetPathButton.isEnabled = false

        binding.saveButton.isEnabled = false
    }


    private fun showErrorMessage(errorMessage: TextMessage) {
        binding.errorMessage.text = errorMessage.get(requireContext())
        binding.errorMessage.visibility = View.VISIBLE
    }

    private fun hideErrorMessage() {
        binding.errorMessage.visibility = View.GONE
    }


    companion object {

        private const val TASK_ID = "TASK_ID"

        fun create(): TaskEditFragment =
            createFragment(null)

        fun create(taskId: String): TaskEditFragment =
            createFragment(taskId)

        private fun createFragment(taskId: String?) = TaskEditFragment().apply {
                arguments = bundleOf(TASK_ID to taskId)
        }
    }
}