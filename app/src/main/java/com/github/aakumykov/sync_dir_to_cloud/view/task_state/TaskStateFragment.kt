package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskStateBinding
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.ext_functions.showToast

class TaskStateFragment : Fragment(R.layout.fragment_task_state) {

    private var _binding: FragmentTaskStateBinding? = null
    private val binding get() = _binding!!

    private val taskStateViewModel: TaskStateViewModel by viewModels()
    private val navigationViewModel: NavigationViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId: String? = arguments?.getString(KEY_TASK_ID)
        if (null == taskId) {
            showToast(R.string.ERROR_there_is_no_task_id)
            navigationViewModel.navigateBack()
            return
        }

        _binding = FragmentTaskStateBinding.bind(view)

        binding.textView.text = taskId

        binding.composeView.setContent {
            SyncObjects(taskId)
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

@Composable
fun SyncObjects(taskId: String,
                modifier: Modifier = Modifier) {

    val taskStateViewModel: TaskStateViewModel = viewModel(factory = TaskStateViewModel.Factory)

    SyncObjectsList(syncObjectList = taskStateViewModel.listState)
}

@Composable
fun SyncObjectsList(syncObjectList: SyncObjectList, modifier: Modifier = Modifier) {
    LazyColumn {
        syncObjectList.forEach { syncObject ->
            item {
                Text(syncObject.name)
            }
        }
    }
}
