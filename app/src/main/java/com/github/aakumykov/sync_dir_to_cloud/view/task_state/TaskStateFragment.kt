package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskStateBinding

class TaskStateFragment : Fragment(R.layout.fragment_task_state) {

    private var _binding: FragmentTaskStateBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTaskStateBinding.bind(view)

        val taskId: String? = arguments?.getString(KEY_TASK_ID)
        binding.textView.text = taskId
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