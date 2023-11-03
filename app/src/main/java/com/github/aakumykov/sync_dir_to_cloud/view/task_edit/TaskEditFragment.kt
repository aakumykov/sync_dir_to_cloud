package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.AuthListDialog
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.AuthSelectionDialog
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class TaskEditFragment : Fragment(R.layout.fragment_task_edit),
    AuthSelectionDialog.Callback {

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private val taskEditViewModel2: TaskEditViewModel2 by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    private var firstRun: Boolean = true

    private val currentTask: SyncTask
        get() = taskEditViewModel2.syncTask

    private var authSelectionDialog: AuthSelectionDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareLayout(view)
        prepareButtons()
        prepareViewModels()

        reconnectToChildDialog()
        initialFillForm()

        /*val taskId: String? = arguments?.getString(TASK_ID)
        taskEditViewModel.prepare(taskId)*/

        /*pageTitleViewModel.setPageTitle(getString(
            when(taskId) {
                null -> R.string.FRAGMENT_TASK_EDIT_creation_title
                else -> R.string.FRAGMENT_TASK_EDIT_edition_title
            }
        ))*/
    }


    private fun reconnectToChildDialog() {
        childFragmentManager.findFragmentByTag(AuthListDialog.TAG).let { fragment ->
            if (fragment is AuthSelectionDialog) {
                authSelectionDialog = fragment
                authSelectionDialog?.setCallback(this)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun prepareLayout(view: View) {
        _binding = FragmentTaskEditBinding.bind(view)

        binding.sourcePathInput.addTextChangedListener(object: SimpleTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentTask?.sourcePath = s.toString()
            }
        })

        binding.targetPathInput.addTextChangedListener(object: SimpleTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentTask?.targetPath = s.toString()
            }
        })

        binding.intervalHours.addTextChangedListener(object: SimpleTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentTask?.intervalHours = s.toString().toInt()
            }
        })

        binding.intervalMinutes.addTextChangedListener(object: SimpleTextWatcher(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentTask?.intervalMinutes = s.toString().toInt()
            }
        })
    }

    private fun prepareButtons() {
        binding.saveButton.setOnClickListener {
            onSaveButtonClicked()
        }
        binding.cancelButton.setOnClickListener { onCancelButtonClicked() }

        binding.intervalHours.setOnClickListener { onSelectTimeClicked() }
        binding.intervalMinutes.setOnClickListener { onSelectTimeClicked() }
        binding.periodSelectionButton.setOnClickListener { onSelectTimeClicked() }

        binding.authSelectionButton.setOnClickListener { onAuthSelectionClicked() }
    }

    private fun prepareViewModels() {
//        taskEditViewModel.getOpState().observe(viewLifecycleOwner, this::onOpStateChanged)
    }


    private fun onOpStateChanged(opState: OpState) {
        when (opState) {
            is OpState.Idle -> showIdleOpState()
            is OpState.Busy -> showBusyOpState(opState)
            is OpState.Success -> finishWork(opState)
            is OpState.Error -> showErrorOpState(opState)
        }
    }



    private fun onSelectTimeClicked() {

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(12)
            .setMinute(10)
            .setTitleText(R.string.sync_task_regulatiry_picker_title)
            .build()

        picker.addOnPositiveButtonClickListener {
            binding.intervalHours.setText(picker.hour.toString())
            binding.intervalMinutes.setText(picker.minute.toString())
        }

        picker.showNow(childFragmentManager, "")
    }

    private fun onAuthSelectionClicked() {
        val authListDialog = AuthListDialog()
        authListDialog.show(childFragmentManager, AuthListDialog.TAG)
        authListDialog.setCallback(this@TaskEditFragment)

        /*with(AuthListDialog()) {
            setCallback(this@TaskEditFragment)
            show(childFragmentManager, AuthListDialog.TAG)
        }*/
    }

    private fun onSaveButtonClicked() {
        taskEditViewModel2.saveSyncTask()
    }

    private fun onCancelButtonClicked() {
        navigationViewModel.navigateTo(NavTarget.Back)
    }

    private fun finishWork(successOpState: OpState.Success) {
        Toast.makeText(requireContext(), successOpState.textMessage.get(requireContext()), Toast.LENGTH_SHORT).show()
        navigationViewModel.navigateBack()
    }



    private fun initialFillForm() {
        if (firstRun) {
            firstRun = false;
            currentTask.let {
                fillPaths(it)
                fillPeriodView(it)
                fillAuthButton(it)
            }
        }
    }

    private fun fillPaths(syncTask: SyncTask) {
        binding.sourcePathInput.setText(syncTask.sourcePath)
        binding.targetPathInput.setText(syncTask.targetPath)
    }

    private fun fillPeriodView(syncTask: SyncTask) {
        binding.intervalHours.setText(syncTask.intervalHours.toString())
        binding.intervalMinutes.setText(syncTask.intervalMinutes.toString())
    }

    private fun fillAuthButton(syncTask: SyncTask) {
        // TODO: сделать
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

        binding.sourcePathSelectionButton.isEnabled = true
        binding.targetPathSelectionButton.isEnabled = true

        binding.saveButton.isEnabled = true
    }

    private fun disableForm() {
        binding.sourcePathInput.isEnabled = false
        binding.targetPathInput.isEnabled = false

        binding.sourcePathSelectionButton.isEnabled = false
        binding.targetPathSelectionButton.isEnabled = false

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

    override fun onCloudAuthSelected(cloudAuth: CloudAuth) {
        currentTask.cloudAuthId = cloudAuth.id
        updateAuthSelectionButton(cloudAuth)
    }

    private fun updateAuthSelectionButton(cloudAuth: CloudAuth) {
        binding.authSelectionButton.text = cloudAuth.name
    }
}