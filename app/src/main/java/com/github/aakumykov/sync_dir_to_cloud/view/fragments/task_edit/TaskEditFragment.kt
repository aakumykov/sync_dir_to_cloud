package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding
import com.github.aakumykov.sync_dir_to_cloud.view.NavBack
import com.github.aakumykov.sync_dir_to_cloud.view.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.PageTitleViewModel

class TaskEditFragment constructor(val id: String?) : Fragment() {

    constructor() : this(null)

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private val taskEditViewModel: TaskEditViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)

        binding.saveButton.setOnClickListener {
            /*taskEditViewModel.onSaveButtonClicked(SyncTask(

            ))*/
        }

        binding.cancelButton.setOnClickListener {
            navigationViewModel.navigateTo(NavBack)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageTitleViewModel.setPageTitle(id ?: getString(R.string.FRAGMENT_TASK_EDIT_creation_title))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun create(id: String?) : TaskEditFragment = TaskEditFragment(id)
        fun create() : TaskEditFragment = TaskEditFragment()
    }
}