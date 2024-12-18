package com.github.aakumykov.sync_dir_to_cloud.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.config.Constants.DEFAULT_BACK_STACK_NAME
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuHelper
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.LogOfSyncFragment
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogFragment
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogFragmentRV
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_state.TaskStateFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()
    private val menuStateViewModel: MenuStateViewModel by viewModels()

    private lateinit var onBackStackChangedListener: OnBackStackChangedListener

    private val menuHelper: MenuHelper by lazy { MenuHelper(this@MainActivity, R.color.onPrimary, R.color.primary) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareLayout()
        prepareFragmentManager()

        subscribeToPageTitle()
        subscribeToPageNavigation()
        // Подписка на меню производится в onCreateOptionsMenu()
    }


    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadInitialFragment(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseFragmentManager()
    }


    override fun onSupportNavigateUp(): Boolean {
        navigationViewModel.navigateBack()
        return true
    }

    private fun loadInitialFragment(intent: Intent?) {
        setFragment(
            when(intent?.action) {
                ACTION_SHOW_TASK_STATE -> TaskStateFragment.create(intent)
                else -> TaskListFragment.create()
            }
        )
    }



    private fun prepareLayout() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    private fun subscribeToPageTitle() {
        pageTitleViewModel.getPageTitle().observe(this, this::onPageTitleChanged)
    }

    private fun subscribeToPageNavigation() {
        navigationViewModel.getNavigationTargetEvents().observe(this, this::onNewNavTarget)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        subscribeToMenuState()
        return super.onCreateOptionsMenu(menu)
    }

    private fun subscribeToMenuState() {
        menuStateViewModel.menuState.observe(this, this::onMenuStateChanged)
    }

    private fun onMenuStateChanged(menuState: MenuState) {
        updateHomeIcon()
        binding.toolbar.menu.also { menu ->
            menu.clear()
            menuHelper.generateMenu(menu, menuState.menuItems, false)
        }
    }

    private fun prepareFragmentManager() {

        onBackStackChangedListener = OnBackStackChangedListener {
            updateHomeIcon()
        }.also {
            supportFragmentManager.addOnBackStackChangedListener(it)
        }
    }

    private fun updateHomeIcon() {
        supportActionBar?.also { actionBar ->
            if (0 == supportFragmentManager.backStackEntryCount) {
                actionBar.setDisplayHomeAsUpEnabled(false)
            }
            else {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_page_back)
            }
        }
    }


    private fun releaseFragmentManager() {
        supportFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
    }



    private fun onPageTitleChanged(title: String) {
        binding.toolbar.title = title
    }

    private fun onNewNavTarget(navTarget: NavTarget) {
        when (navTarget) {
            is NavTarget.Add -> loadFragment(TaskEditFragment.create())
            is NavTarget.Edit -> loadFragment(TaskEditFragment.create(navTarget.id))
            is NavTarget.Back -> returnToPrevFragment()
            is NavTarget.TaskInfo -> loadFragment(TaskStateFragment.create(navTarget.id))
            is NavTarget.SyncLog -> {
                loadFragment(SyncLogFragmentRV.create(navTarget.taskId, navTarget.executionId))
//                loadFragment(LogOfSyncFragment.create(navTarget.taskId, navTarget.executionId))
            }
            else -> loadInitialFragment(intent)
        }
    }

    private fun setFragment(fragment: Fragment) {

        // Не удалять "as FragmentManager"
        (supportFragmentManager as FragmentManager).clearBackStack(DEFAULT_BACK_STACK_NAME)

        supportFragmentManager.beginTransaction()
//            .setReorderingAllowed(false)
            .replace(R.id.fragmentContainerView, fragment, null)
            .commitNow()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(DEFAULT_BACK_STACK_NAME)
//            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }

    private fun returnToPrevFragment() {
        supportFragmentManager.popBackStack()
    }

    private fun updateMenu() {

    }


    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        const val ACTION_SHOW_TASK_STATE: String = "SHOW_TASK_STATE"
    }
}