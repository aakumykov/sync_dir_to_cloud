package com.github.aakumykov.sync_dir_to_cloud.view.task_info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskInfoBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.utils.ListViewAdapter
import kotlinx.coroutines.launch

class TaskInfoFragment : Fragment(R.layout.fragment_task_info) {

    private var _binding: FragmentTaskInfoBinding? = null
    private val binding get() = _binding!!

    // TODO: внудрять ViewModel-и Dagger-ом?
    private lateinit var mTaskInfoViewModel: TaskInfoViewModel
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

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

        _binding = FragmentTaskInfoBinding.bind(view)
        binding.taskIdView.text = taskId

        listAdapter = ListViewAdapter(requireContext(), R.layout.sync_object_list_item, R.id.title, syncObjectList)
        binding.listView.adapter = listAdapter

        mTaskInfoViewModel = DaggerViewModelHelper.get(this, TaskInfoViewModel::class.java)

        lifecycleScope.launch {
            mTaskInfoViewModel.getSyncObjectList(taskId).observe(viewLifecycleOwner) { list ->
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

        fun create(taskId: String?): TaskInfoFragment {
            return TaskInfoFragment().apply {
                arguments = Bundle().apply { putString(KEY_TASK_ID, taskId) }
            }
        }
    }
}