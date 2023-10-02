package com.github.aakumykov.sync_dir_to_cloud.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var fragmentManager: androidx.fragment.app.FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationViewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        navigationViewModel.getNavigationTargetEvents().observe(this, this::onNewNavTarget)

        fragmentManager = supportFragmentManager
    }

    private fun onNewNavTarget(navTarget: NavTarget) {
        when (navTarget) {
            NavStart, NavList -> setFragment(TaskListFragment.create())
            NavAdd -> loadFragment(TaskEditFragment.create())
            NavEdit -> loadFragment(TaskEditFragment.create((NavEdit) navTarget.id))
        }
    }

    private fun setFragment(fragment: Fragment) {

    }
}