package com.github.aakumykov.sync_dir_to_cloud.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.fragments.TaskListFragment

class MainActivity : AppCompatActivity() {

    private val DEFAULT_BACK_STACK_NAME = "default_back_stack"
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
//            is NavStart, NavList -> setFragment(TaskListFragment.create())
            is NavAdd -> loadFragment(TaskEditFragment.create())
            is NavEdit -> loadFragment(TaskEditFragment.create(navTarget.id))
            else -> setFragment(TaskListFragment.create())
        }
    }

    private fun setFragment(fragment: Fragment) {

        fragmentManager.clearBackStack(DEFAULT_BACK_STACK_NAME)

        fragmentManager.beginTransaction()
            .setReorderingAllowed(false)
            .replace(R.id.fragmentContainerView, fragment, null)
            .commitNow()
    }

    private fun loadFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .addToBackStack(DEFAULT_BACK_STACK_NAME)
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}