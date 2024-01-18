package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskStateBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

class TaskStateFragment : Fragment(R.layout.fragment_task_state) {

    private var _binding: FragmentTaskStateBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskStateViewModel: TaskStateViewModel
    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()

    private lateinit var listAdapter: ListViewAdapter<SyncObject>
    private val syncObjectList: MutableList<SyncObject> = mutableListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_INFO_title))

        val taskId: String? = arguments?.getString(KEY_TASK_ID)
        if (null == taskId) {
            showToast(R.string.there_is_no_task_id)
            navigationViewModel.navigateBack()
            return
        }

        _binding = FragmentTaskStateBinding.bind(view)
        binding.taskIdView.text = taskId

        listAdapter = ListViewAdapter(requireContext(), R.layout.sync_object_list_item, R.id.title, syncObjectList)
        binding.listView.adapter = listAdapter

        taskStateViewModel = DaggerViewModelHelper.get(this, TaskStateViewModel::class.java)

        lifecycleScope.launch {
            taskStateViewModel.getSyncObjectList(taskId).observe(viewLifecycleOwner) { list ->
                syncObjectList.clear()
                syncObjectList.addAll(list)
                listAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        const val KEY_TASK_ID = "TASK_ID"

        fun create(taskId: String?): TaskStateFragment {
            return TaskStateFragment().apply {
                arguments = Bundle().apply { putString(KEY_TASK_ID, taskId) }
            }
        }
    }
}