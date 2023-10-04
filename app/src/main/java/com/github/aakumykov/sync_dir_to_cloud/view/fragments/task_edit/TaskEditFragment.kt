package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTaskBase

import com.github.aakumykov.sync_dir_to_cloud.view.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.TextMessage
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.operation_state.OpState

class TaskEditFragment constructor(val id: String?) : Fragment() {

    constructor() : this(null)

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!
    private val taskEditViewModel: TaskEditViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)

        binding.saveButton.setOnClickListener {
            taskEditViewModel.createOrSaveSyncTask(
                SyncTaskBase(
                    binding.sourcePathInput.text.toString(),
                    binding.targetPathInput.text.toString()
                )
            )
        }

        binding.cancelButton.setOnClickListener {
            navigationViewModel.navigateTo(NavTarget.Back)
        }

        taskEditViewModel.getCurrentTask().observe(viewLifecycleOwner, this::onCurrentTaskChanged)
        taskEditViewModel.getOperationState().observe(viewLifecycleOwner, this::onOperationStateChanged)

        return binding.root
    }


    private fun onOperationStateChanged(opState: OpState) {
        when (opState) {
            is OpState.Idle -> showIdleOpState()
            is OpState.Busy -> showBusyOpState(opState)
            is OpState.Success -> finishWork()
            is OpState.Error -> showErrorOpState(opState)
        }
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

    private fun finishWork() {
        Toast.makeText(requireContext(), getString(R.string.sync_task_created), Toast.LENGTH_SHORT).show()
        navigationViewModel.navigateBack()
    }



    private fun onCurrentTaskChanged(syncTask: SyncTask) {
        fillForm(syncTask)
    }

    private fun fillForm(syncTask: SyncTask) {
        binding.sourcePathInput.setText(syncTask.sourcePath)
        binding.targetPathInput.setText(syncTask.targetPath)
//        binding.regularityInput.setText(syncTask.regularity.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageTitleViewModel.setPageTitle(id ?: getString(R.string.FRAGMENT_TASK_EDIT_creation_title))

        // TODO: преобразовать в один метод "prepare"
        if (null == id)
            taskEditViewModel.prepareForNewTask()
        else
            taskEditViewModel.loadTask(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        fun create(id: String?) : TaskEditFragment = TaskEditFragment(id)
        fun create() : TaskEditFragment = TaskEditFragment()
    }
}