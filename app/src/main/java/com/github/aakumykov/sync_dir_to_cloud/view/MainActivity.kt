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
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.extensions.openAppProperties
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.StorageAccessViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.CustomMenuItem
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuHelper
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_state.TaskStateFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()
    private val menuStateViewModel: MenuStateViewModel by viewModels()

    private lateinit var onBackStackChangedListener: OnBackStackChangedListener
    private lateinit var fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks

    private lateinit var storageAccessHelper: StorageAccessHelper
    private val storageAccessViewModel: StorageAccessViewModel by viewModels()

    private val menuHelper: MenuHelper by lazy { MenuHelper(this@MainActivity, R.color.onPrimary, R.color.primary) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storageAccessHelper = StorageAccessHelper.create(this)
        storageAccessViewModel.storageAccessRequest.observe(this) {
            storageAccessHelper.requestStorageAccess {
                storageAccessViewModel.setStorageAccessResult(it)
            }
        }

        prepareLayout()
        prepareButtons()
        prepareViewModels()
        prepareFragmentManager()
    }

    override fun onStart() {
        super.onStart()
        createProbeMenu()
    }

    private fun prepareButtons() {
        createProbeMenu()
    }

    private fun createProbeMenu() {
        binding.generateMenuButton.setOnClickListener {
            menuHelper.generateMenu(
                binding.toolbar.menu,
                arrayOf(
                    CustomMenuItem(R.id.actionAppProperties, R.string.MENU_ITEM_app_properties, R.drawable.ic_app_properties, { openAppProperties() })
                )
            )
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }


    private fun prepareLayout() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    private fun prepareViewModels() {
        navigationViewModel.getNavigationTargetEvents().observe(this, this::onNewNavTarget)
        pageTitleViewModel.getPageTitle().observe(this, this::onPageTitleChanged)
        menuStateViewModel.menuState.observe(this, this::onMenuStateChanged)
    }

    private fun onMenuStateChanged(menuState: MenuState) {
        /*binding.toolbar.menu.also { menu ->
            menu.clear()
            menuHelper.generateMenu(menu, menuState.menuItems, false)
            *//*menuState.menuItems.forEach { customMenuItem ->
                menu.add(0, customMenuItem.id, 0, customMenuItem.title).also { menuItem ->
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    menuItem.setIcon(customMenuItem.icon)
                    menuItem.setOnMenuItemClickListener { customMenuItem.action.run(); true }
                }
            }*//*
        }*/
    }

    private fun prepareFragmentManager() {

        onBackStackChangedListener = OnBackStackChangedListener {

            if (0 == supportFragmentManager.backStackEntryCount) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            else {
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_page_back)
                }
            }
        }.also {
            supportFragmentManager.addOnBackStackChangedListener(it)
        }
    }


    private fun releaseFragmentManager() {
        supportFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
    }



    private fun onPageTitleChanged(title: String) {
        setTitle(title)
    }

    private fun onNewNavTarget(navTarget: NavTarget) {
        when (navTarget) {
            is NavTarget.Add -> loadFragment(TaskEditFragment.create())
            is NavTarget.Edit -> loadFragment(TaskEditFragment.create(navTarget.id))
            is NavTarget.Back -> returnToPrevFragment()
            is NavTarget.TaskInfo -> loadFragment(TaskStateFragment.create(navTarget.id))
            else -> loadInitialFragment(intent)
        }
    }

    private fun setFragment(fragment: Fragment) {

        // Не удалять "as FragmentManager"
        (supportFragmentManager as FragmentManager).clearBackStack(DEFAULT_BACK_STACK_NAME)

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(false)
            .replace(R.id.fragmentContainerView, fragment, null)
            .commitNow()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(DEFAULT_BACK_STACK_NAME)
            .setReorderingAllowed(true)
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

        private const val DEFAULT_BACK_STACK_NAME = "default_back_stack"
    }
}