package com.github.aakumykov.sync_dir_to_cloud.view.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.ItemClickCallback
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view.TaskListAdapter
import kotlinx.coroutines.launch

class TaskListFragment : Fragment(), ItemClickCallback {

    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }

    // View binding
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // RecyclerView adapter
    private var taskListAdapter: TaskListAdapter? = null

    // ViewModels
    private val taskListViewModel: TaskListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        prepareLayout(inflater, container)
        prepareAdapter()
        prepareRecyclerView()
        prepareButtons()
        prepareViewModels()
        setPageTitle()
        return binding.root
    }


    override fun onDestroyView() {
        taskListAdapter = null
        _binding = null
        super.onDestroyView()
    }


    override fun onTaskEditClicked(taskId: String) {
        navigationViewModel.navigateTo(NavTarget.Edit(taskId))
    }

    override fun onTaskRunClicked(taskId: String) {
        taskListViewModel.runTask(taskId)
    }

    override fun onTaskDeleteClicked(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskInfoClicked(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun onTaskEnableSwitchClicked(taskId: String) {
        taskListViewModel.changeTaskEnabled(taskId)
    }


    private fun prepareLayout(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
    }


    private fun prepareAdapter() {
        taskListAdapter = TaskListAdapter(this)
    }


    private fun prepareRecyclerView() {
        binding.recyclerView.adapter = taskListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    private fun prepareViewModels() {
        lifecycleScope.launch {
            taskListViewModel.getTaskList().observe(viewLifecycleOwner) { onListChanged(it) }
        }
    }


    private fun setPageTitle() {
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_LIST_title))
    }


    private fun prepareButtons() {
        binding.addButton.setOnClickListener { onAddClicked() }
    }


    private fun onListChanged(list: List<SyncTask>) {
        taskListAdapter?.setList(list)
    }


    private fun onAddClicked() {
        navigationViewModel.navigateTo(NavTarget.Add)
    }
}

