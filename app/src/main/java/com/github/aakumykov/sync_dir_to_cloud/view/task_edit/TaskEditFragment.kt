package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.file_lister.FSItem
import com.github.aakumykov.file_selector.FileSelectionDialog
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
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.utils.SimpleTextWatcher
import com.github.aakumykov.sync_dir_to_cloud.view.utils.TextMessage
import com.github.aakumykov.yandex_disk_file_selector.YandexDiskFileSelector
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class TaskEditFragment : Fragment(R.layout.fragment_task_edit),
    AuthSelectionDialog.Callback, FileSelectionDialog.Callback {

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private val taskEditViewModel: TaskEditViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    private var firstRun: Boolean = true
    private val currentTask get(): SyncTask? = taskEditViewModel.currentTask

    private var authSelectionDialog: AuthSelectionDialog? = null

    private var currentCloudAuth: CloudAuth? = null
    private var yandexDiskFileSelector: FileSelectionDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstRun = (null == savedInstanceState)

        prepareLayout(view)
        prepareButtons()
        prepareViewModels()

        reconnectToChildDialogs()
        prepareForCreationOfEdition()
    }


    private fun prepareViewModels() {
        taskEditViewModel.getOpState().observe(viewLifecycleOwner, ::onOpStateChanged)
        taskEditViewModel.syncTaskLiveData.observe(viewLifecycleOwner, ::onSyncTaskChanged)
        taskEditViewModel.cloudAuthLiveData.observe(viewLifecycleOwner, ::onCloudAuthChanged)
    }

    private fun onSyncTaskChanged(syncTask: SyncTask?) {
        if (null != syncTask)
            fillForm(syncTask)
    }

    private fun onCloudAuthChanged(cloudAuth: CloudAuth?) {
        cloudAuth?.let { onCloudAuthSelected(cloudAuth) }
    }

    private fun prepareForCreationOfEdition() {

        if (firstRun) {
            val taskId = arguments?.getString(TASK_ID)

            if (null != taskId) {
                taskEditViewModel.prepareForEdit(taskId)
                pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_EDIT_edition_title))
            } else {
                taskEditViewModel.prepareForCreate()
                pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_EDIT_creation_title))
            }
        }
    }


    private fun reconnectToChildDialogs() {
        childFragmentManager.findFragmentByTag(AuthListDialog.TAG).let { fragment ->
            if (fragment is AuthSelectionDialog) {
                authSelectionDialog = fragment
                authSelectionDialog?.setCallback(this)
            }
        }

        FileSelectionDialog.find(parentFragmentManager)?.let {
            if (it is YandexDiskFileSelector)
                it.setCallback(this)
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
        binding.sourcePathSelectionButton.setOnClickListener { onSourcePathSelectionClicked() }

        binding.saveButton.setOnClickListener { onSaveButtonClicked() }
        binding.cancelButton.setOnClickListener { onCancelButtonClicked() }

        binding.intervalHours.setOnClickListener { onSelectTimeClicked() }
        binding.intervalMinutes.setOnClickListener { onSelectTimeClicked() }
        binding.periodSelectionButton.setOnClickListener { onSelectTimeClicked() }

        binding.authSelectionButton.setOnClickListener { onAuthSelectionClicked() }
    }


    private fun onSourcePathSelectionClicked() {
        if (null == currentCloudAuth) {
            showToast(R.string.TOAST_select_cloud_auth_first)
            // TODO: помигать соответствующей кнопкой
            return
        }

        currentCloudAuth?.authToken?.let { token ->
            // TODO: режим выбора каталога, а не файла
            yandexDiskFileSelector = YandexDiskFileSelector.create(token)
            yandexDiskFileSelector?.setCallback(this)
            yandexDiskFileSelector?.show(parentFragmentManager)
        }
    }

    override fun onConfirmSelectionClicked(selectedItemsList: List<FSItem>) {
        currentTask?.sourcePath = selectedItemsList[0].path
    }


    private fun onOpStateChanged(opState: OpState) {
        when (opState) {
            is OpState.Idle -> showIdleOpState()
            is OpState.Busy -> showBusyOpState(opState)
            is OpState.Error -> showErrorOpState(opState)
            is OpState.Success -> finishWork(opState)
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
        taskEditViewModel.saveSyncTask()
    }

    private fun onCancelButtonClicked() {
        navigationViewModel.navigateTo(NavTarget.Back)
    }

    private fun finishWork(successOpState: OpState.Success) {
        Toast.makeText(requireContext(), successOpState.textMessage.get(requireContext()), Toast.LENGTH_SHORT).show()
        navigationViewModel.navigateBack()
    }

    private fun fillForm(syncTask: SyncTask) {
        fillPaths(syncTask)
        fillPeriodView(syncTask)
        fillAuthButton(syncTask)
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
        binding.authSelectionButton.text = syncTask.cloudAuthId
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

    override fun onCloudAuthSelected(cloudAuth: CloudAuth) {
        currentTask?.cloudAuthId = cloudAuth.id
        currentCloudAuth = cloudAuth
        displayCLoudAuthSelectionState(cloudAuth)
    }

    private fun displayCLoudAuthSelectionState(cloudAuth: CloudAuth) {
        binding.authSelectionButton.text = cloudAuth.name
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