package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list

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
import com.github.aakumykov.sync_dir_to_cloud.view.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list.recycler_view.TaskListAdapter
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }

    // View binding
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    // RecyclerView adapter
    private val taskListAdapter: TaskListAdapter by lazy { TaskListAdapter() }

    // ViewModels
    private val taskListViewModel: TaskListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        prepareLayout(inflater, container)
        prepareRecyclerView()
        prepareButtons()
        prepareViewModels()
        return binding.root
    }


    private fun prepareViewModels() {
        lifecycleScope.launch {
            taskListViewModel.getTaskList().observe(viewLifecycleOwner) { onListChanged(it) }
        }
    }


    private fun prepareLayout(inflater: LayoutInflater, container: ViewGroup?) {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
    }


    private fun prepareButtons() {
        binding.addButton.setOnClickListener { onAddClicked() }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_LIST_title))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun prepareRecyclerView() {
        binding.recyclerView.adapter = taskListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    private fun onListChanged(list: List<SyncTask>) {
        taskListAdapter.setList(list)
    }


    private fun onAddClicked() {
        navigationViewModel.navigateTo(NavTarget.Add)
    }
}

