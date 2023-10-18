package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEdit2Binding

class TaskEditFragment2 : Fragment() {

    private var _binding: FragmentTaskEdit2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskEdit2Binding.inflate(inflater, container, false)

        val view = binding.root

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    TaskEditFragment2MainContent()
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TASK_ID = "TASK_ID"
        fun create(): TaskEditFragment2 {
            return TaskEditFragment2()
        }
        fun create(taskId: String): TaskEditFragment2 {
            return TaskEditFragment2().apply {
                arguments = Bundle().apply {
                    putString(TASK_ID, taskId)
                }
            }
        }
    }
}