package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskListBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list.recycler_view.TaskListAdapter

class TaskListFragment : Fragment() {

    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskListAdapter: TaskListAdapter

    private val taskListViewModel: TaskListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        prepareRecyclerView()

        binding.addButton.setOnClickListener { navigationViewModel.navigateTo(NavTarget.Add) }

        return binding.root
    }

    private fun prepareRecyclerView() {
        taskListAdapter = TaskListAdapter()
        binding.recyclerView.adapter = taskListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        taskListViewModel.getTaskList().observe(viewLifecycleOwner, taskListObserver)
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_LIST_title))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onListChanged(list: List<SyncTask>) {
        taskListAdapter.setList(list)
    }
}

