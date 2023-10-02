package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskListBinding
import com.github.aakumykov.sync_dir_to_cloud.view.NavAdd
import com.github.aakumykov.sync_dir_to_cloud.view.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.PageTitleViewModel
import com.gitlab.aakumykov.simple_list_view_driver.SimpleListViewDriver
import com.gitlab.aakumykov.simple_list_view_driver.iTitleItem

class TaskListFragment : Fragment() {

    companion object {
        fun create() : TaskListFragment = TaskListFragment()
    }

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val taskListViewModel: TaskListViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by activityViewModels()
    private val pageTitleViewModel: PageTitleViewModel by activityViewModels()

    private val simpleListViewDriver by lazy {
        SimpleListViewDriver(binding.listView)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        binding.addButton.setOnClickListener { navigationViewModel.navigateTo(NavAdd) }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskListViewModel.getTaskList().observe(viewLifecycleOwner, this::onListChanged)
        pageTitleViewModel.setPageTitle(getString(R.string.FRAGMENT_TASK_LIST_title))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onListChanged(list: List<iTitleItem>) {
        simpleListViewDriver.addList(list)
    }
}

