package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.file_lister_navigator_selector.extensions.listenForFragmentResult
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.factories.file_selector.FileSelectorFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.AuthListDialog
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.EndpointType
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.StorageTypeIconProvider
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state.OpState
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.SimpleTextWatcher
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TaskEditFragment : Fragment(R.layout.fragment_task_edit) {

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskEditViewModel: TaskEditViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    private lateinit var storageAccessHelper: StorageAccessHelper

    private var firstRun: Boolean = true
    private val currentTask get(): SyncTask? = taskEditViewModel.currentTask

    /*private val sourceReaderCreator: StorageReaderCreator
        get() = App.getAppComponent().getStorageReaderCreator()

    private val targetWriterCreator: StorageWriterCreator
        get() = App.getAppComponent().getStorageWriterCreator()*/

    private val cloudAuthReader: CloudAuthReader
        get() = App.getAppComponent().getCloudAuthReader()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstRun = (null == savedInstanceState)

        prepareViewModels()

        prepareStorageAccessHelper()
        prepareFragmentResultListeners()
        prepareForCreationOfEdition()

        prepareLayout(view)
        prepareButtons()
        /**
         * [initSpinner] будет вызван в методе [fillForm]
         * после поступления SyncTask от ViewModel, в методе [onSyncTaskChanged]
          */
    }

    private fun initSpinner() {

        val syncModes: Array<SyncMode> = SyncMode.entries.toTypedArray()

        val spinnerAdapter: ArrayAdapter<SyncMode> = ArrayAdapter<SyncMode>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            syncModes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val syncMode = currentTask?.syncMode ?: SyncMode.SYNC
        val syncModeIndex = syncModes.indexOf(syncMode)

        binding.syncModeSpinner.apply {
            adapter = spinnerAdapter
            prompt = "Режим работы:"
            setSelection(syncModeIndex)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    taskEditViewModel.currentTask?.syncMode = syncModes[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    taskEditViewModel.currentTask?.syncMode = null
                }
            }
        }
    }

    private fun prepareStorageAccessHelper() {
        storageAccessHelper = StorageAccessHelper.Companion.create(this)
    }

    private fun prepareFragmentResultListeners() {

        listenForFragmentResult(AuthListDialog.KEY_SELECT_CLOUD_AUTH) { _, fragmentResult ->
            onCloudAuthSelectionResult(fragmentResult)
        }

        listenForFragmentResult(SOURCE_PATH_SELECTION_REQUEST_KEY) { _, fragmentResult ->
            onSourcePathSelectionResult(fragmentResult)
        }

        listenForFragmentResult(TARGET_PATH_SELECTION_REQUEST_KEY) { _, fragmentResult ->
            onTargetPathSelectionResult(fragmentResult)
        }
    }

    private fun onSourcePathSelectionResult(fragmentResult: Bundle) {
        extractSelectedItems(fragmentResult) { list ->
            onSourcePathSelected(list?.firstOrNull())
        }
    }

    private fun onTargetPathSelectionResult(fragmentResult: Bundle) {
        extractSelectedItems(fragmentResult) { list ->
            onTargetPathSelected(list?.firstOrNull())
        }
    }

    private fun extractSelectedItems(fragmentResult: Bundle, block: (list: List<FSItem>?) -> Unit) {
        FileSelector.extractSelectionResult(fragmentResult).also(block)
    }

    private fun onTargetPathSelected(fsItem: FSItem?) {
        fsItem?.absolutePath.also { path ->
            currentTask?.targetPath = path
            binding.targetPathInput.setText(path)
        }
    }

    private fun onSourcePathSelected(fsItem: FSItem?) {
        fsItem?.absolutePath.also { path ->
            currentTask?.sourcePath = path
            binding.sourcePathInput.setText(path)
        }
    }

    private fun prepareViewModels() {
        taskEditViewModel = DaggerViewModelHelper.get(this, TaskEditViewModel::class.java)

        taskEditViewModel.getOpState().observe(viewLifecycleOwner, ::onOpStateChanged)
        taskEditViewModel.syncTaskLiveData.observe(viewLifecycleOwner, ::onSyncTaskChanged)
    }


    private fun onSyncTaskChanged(syncTask: SyncTask?) {
        syncTask?.also { task ->
            fillForm(task)
        }
    }

    private fun requestCloudAuth(cloudAuthId: String?) {
        lifecycleScope.launch {
            taskEditViewModel.getCloudAuth(cloudAuthId)?.also { cloudAuth ->
//                onCloudAuthSelectionChanged(cloudAuth)
            }
        }
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

        binding.sourceTypeButton.setOnClickListener { selectSourceStorageType() }
        binding.targetTypeButton.setOnClickListener { selectTargetStorageType() }

        binding.sourcePathSelectionButton.setOnClickListener { onSelectSourcePathClicked() }
        binding.targetPathSelectionButton.setOnClickListener { onSelectTargetPathClicked() }

        binding.saveButton.setOnClickListener { onSaveButtonClicked() }
        binding.cancelButton.setOnClickListener { onCancelButtonClicked() }

        binding.intervalHours.setOnClickListener { onSelectTimeClicked() }
        binding.intervalMinutes.setOnClickListener { onSelectTimeClicked() }
        binding.periodSelectionButton.setOnClickListener { onSelectTimeClicked() }

        binding.intervalHours.doOnTextChanged { text, start, before, count ->
            taskEditViewModel.setIntervalHours(text.toString().toInt())
        }

        binding.intervalMinutes.doOnTextChanged { text, start, before, count ->
            taskEditViewModel.setIntervalMinutes(text.toString().toInt())
        }
    }


    private fun selectSourceStorageType() {
        showAuthSelectionDialog(EndpointType.SOURCE, true)
    }

    private fun selectTargetStorageType() {
        showAuthSelectionDialog(EndpointType.TARGET, true)
    }


    private fun onSelectSourcePathClicked() {
        if (null != currentTask && null != currentTask?.sourceStorageType)
            selectSourcePath()
        else
            selectSourceStorageType()
    }

    private fun onSelectTargetPathClicked() {
        if (null != currentTask && null != currentTask?.targetStorageType)
            selectTargetPath()
        else
            selectTargetStorageType()
    }


    private fun selectSourcePath() {
        currentTask?.also { syncTask ->
            syncTask.sourceAuthId?.also { sourceAuthId ->
                syncTask.sourceStorageType?.also { storageType ->

                    lifecycleScope.launch(Dispatchers.IO) {

                        cloudAuthReader.getCloudAuth(sourceAuthId)?.also { cloudAuth ->

                            withContext(Dispatchers.Main) {

                                FileSelectorFactory()
                                    .create(
                                        SOURCE_PATH_SELECTION_REQUEST_KEY,
                                        storageType,
                                        cloudAuth
                                    )
                                    .show(childFragmentManager, FileSelector.TAG)

                            }
                        }

                    }
                }
            }
        }
    }


    private fun selectTargetPath() {
        currentTask?.also { syncTask ->
            syncTask.targetAuthId?.also { targetAuthId ->
                syncTask.targetStorageType?.also { storageType ->

                    lifecycleScope.launch(Dispatchers.IO) {

                        cloudAuthReader.getCloudAuth(targetAuthId)?.also { cloudAuth ->

                            withContext(Dispatchers.Main) {

                                FileSelectorFactory()
                                    .create(
                                        TARGET_PATH_SELECTION_REQUEST_KEY,
                                        storageType,
                                        cloudAuth
                                    )
                                    .show(childFragmentManager, FileSelector.TAG)

                            }
                        }

                    }
                }
            }
        }
    }


    private fun onOpStateChanged(opState: OpState) {
        when (opState) {
            is OpState.Busy -> showBusyOpState(opState)
            is OpState.Error -> showErrorOpState(opState)
            is OpState.Success -> finishWork(opState)
            else -> showIdleOpState()
        }
    }



    private fun onSelectTimeClicked() {

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(currentTask?.intervalHours ?: 0)
            .setMinute(currentTask?.intervalMinutes ?: 0)
            .setTitleText(R.string.sync_task_regulatiry_picker_title)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            binding.intervalHours.setText(timePicker.hour.toString())
            binding.intervalMinutes.setText(timePicker.minute.toString())
        }

        timePicker.showNow(childFragmentManager, "")
    }

    private fun showAuthSelectionDialog(endpointType: EndpointType, openFileSelectorOnFinish: Boolean) {
        AuthListDialog
            .create(endpointType, openFileSelectorOnFinish)
            .show(childFragmentManager, AuthListDialog.TAG)
    }

    private fun onCloudAuthSelectionResult(fragmentResult: Bundle) {

        AuthListDialog.extractCloudAuth(fragmentResult)?.also { cloudAuth ->
            fragmentResult.getString(AuthListDialog.ENDPOINT_TYPE)
                ?.let { EndpointType.valueOf(it) }
                ?.also { endpointType ->

                    when (endpointType) {
                        EndpointType.SOURCE -> { taskEditViewModel.setSourceAuthAndType(cloudAuth) }
                        EndpointType.TARGET -> { taskEditViewModel.setTargetAuthAndType(cloudAuth) }
                    }

                    if (fragmentResult.getBoolean(AuthListDialog.WITH_NEXT_ACTION, false)) {
                        when(endpointType) {
                            EndpointType.SOURCE -> selectSourcePath()
                            EndpointType.TARGET -> selectTargetPath()
                        }
                    }
                }
        }
    }

    private fun onSaveButtonClicked() {

        // TODO: нормальная валидация

        val errors: MutableList<String> = mutableListOf()

        binding.sourcePathInput.apply {
            if (text.isNullOrEmpty()) {
                error = resources.getString(R.string.VALIDATION_cannot_be_empty)
                errors.add(this.id.toString())
            } else {
                errors.remove(this.id.toString())
            }
        }

        binding.targetPathInput.apply {
            if (text.isNullOrEmpty()) {
                error = resources.getString(R.string.VALIDATION_cannot_be_empty)
                errors.add(this.id.toString())
            } else {
                errors.remove(this.id.toString())
            }
        }

        if (errors.isEmpty()) {
            taskEditViewModel.saveSyncTask()
        }
    }

    private fun onCancelButtonClicked() {
        navigationViewModel.navigateBack()
    }

    private fun finishWork(successOpState: OpState.Success) {
        Toast.makeText(requireContext(), successOpState.textMessage.get(requireContext()), Toast.LENGTH_SHORT).show()
        navigationViewModel.navigateBack()
    }

    private fun fillForm(syncTask: SyncTask) {
        fillPaths(syncTask)
        fillStorageTypeButtons(syncTask)
        fillPeriodView(syncTask)
        initSpinner()
    }

    private fun fillStorageTypeButtons(syncTask: SyncTask) {
        showOrHideStorageTypeIcon(binding.sourceTypeButton, syncTask.sourceStorageType)
        showOrHideStorageTypeIcon(binding.targetTypeButton, syncTask.targetStorageType)
    }

    private fun showOrHideStorageTypeIcon(imageButton: ImageButton, storageType: StorageType?) {
        if (null == storageType) {
            imageButton.visibility = View.GONE
        } else {
            imageButton.apply {
                setImageResource(StorageTypeIconProvider.getIconFor(storageType))
                visibility = View.VISIBLE
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

    private fun displayCloudAuthState() {
        /*binding.authSelectionButton.apply {
            setText(currentCloudAuth?.name ?: getString(R.string.BUTTON_task_edit_select_cloud_auth))
            setIcon(
                ResourcesCompat.getDrawable(
                    resources,
                    StorageTypeIconGetter.getIconFor(currentTask?.targetType),
                    requireActivity().theme
                )
            )
        }*/
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

        val TAG: String = TaskEditFragment::class.java.simpleName
        const val SOURCE_PATH_SELECTION_REQUEST_KEY = "SOURCE_PATH_SELECTION_REQUEST_KEY"
        const val TARGET_PATH_SELECTION_REQUEST_KEY = "TARGET_PATH_SELECTION_REQUEST_KEY"
        private const val TASK_ID = "TASK_ID"


        fun create(): TaskEditFragment
            = createFragment(null)

        fun create(taskId: String): TaskEditFragment
            = createFragment(taskId)

        private fun createFragment(taskId: String?) = TaskEditFragment().apply {
            arguments = bundleOf(TASK_ID to taskId)
        }
    }
}