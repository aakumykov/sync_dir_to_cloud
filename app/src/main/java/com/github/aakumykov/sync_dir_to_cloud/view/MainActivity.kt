package com.github.aakumykov.sync_dir_to_cloud.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.extensions.openAppProperties
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()
    private lateinit var fragmentManager: androidx.fragment.app.FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navigationViewModel.getNavigationTargetEvents().observe(this, this::onNewNavTarget)
        pageTitleViewModel.getPageTitle().observe(this, this::onPageTitleChanged)

        fragmentManager = supportFragmentManager
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent()")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.actionAppProperties -> {
                openAppProperties()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onPageTitleChanged(title: String) {
        setTitle(title)
    }

    private fun onNewNavTarget(navTarget: NavTarget) {
        when (navTarget) {
//            is NavTarget.Start, NavTarget.List -> setFragment(TaskListFragment.create())
            is NavTarget.Add -> loadFragment(TaskEditFragment.create())
            is NavTarget.Edit -> loadFragment(TaskEditFragment.create(navTarget.id))
            is NavTarget.Back -> returnToPrevFragment()
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

    private fun returnToPrevFragment() {
        fragmentManager.popBackStack()
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        fun simpleLaunchingIntent(context: Context) = Intent(context, MainActivity::class.java)

        const val CODE_SHOW_TASK_STATE: Int = 10

        private const val DEFAULT_BACK_STACK_NAME = "default_back_stack"
    }
}