package com.github.aakumykov.sync_dir_to_cloud.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.databinding.FragmentTaskEditBinding

class TaskEditFragment constructor(val id: String?) : Fragment() {

    constructor() : this(null)

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)
        return binding.root
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