package com.github.aakumykov.sync_dir_to_cloud.view

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import androidx.lifecycle.lifecycleScope
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.config.Constants.DEFAULT_BACK_STACK_NAME
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMainBinding
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavTarget
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuHelper
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.settings.SettingsFragment
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_details.TaskDetailsFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()
    private val menuStateViewModel: MenuStateViewModel by viewModels()

    private lateinit var onBackStackChangedListener: OnBackStackChangedListener

    private val menuHelper: MenuHelper by lazy { MenuHelper(this@MainActivity, R.color.onPrimary, R.color.primary) }

    private lateinit var storageAccessHelper: StorageAccessHelper

    @Inject
    lateinit var cloudAuthReader: CloudAuthReader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "appComponent: ${appComponent.hashCode()}")

        appComponent.injectToMainActivity(this)

        prepareLayout()
        prepareFragmentManager()

        subscribeToPageTitle()
        subscribeToPageNavigation()
        // Подписка на меню производится в onCreateOptionsMenu()

        prepareStorageAccessHelper()
        checkStorageAccessPermission()
    }

    override fun onNewIntent(intent: Intent) {
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
        when(intent?.action) {
            ACTION_SHOW_TASK_STATE -> addFragment(TaskDetailsFragment.create(intent))
            ACTION_SHOW_SYNC_LOG -> addFragment(SyncLogFragment.create(intent.extras))
            else -> setFragment(TaskListFragment.create())
        }
    }



    private fun prepareLayout() {
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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
            is NavTarget.Add -> addFragment(TaskEditFragment.create())
            is NavTarget.Edit -> addFragment(TaskEditFragment.create(navTarget.id))
            is NavTarget.Back -> returnToPrevFragment()
            is NavTarget.TaskInfo -> addFragment(TaskDetailsFragment.create(navTarget.id))
            is NavTarget.SyncLog -> addFragment(SyncLogFragment.create(navTarget.taskId, navTarget.executionId))
            is NavTarget.AppSettings -> addFragment(SettingsFragment.create())
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

    private fun addFragment(fragment: Fragment) {
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

    private fun prepareStorageAccessHelper() {
        storageAccessHelper = StorageAccessHelper.create(this).apply {
            prepareForFullAccess()
        }
    }

    // FIXME: временное решение
    private fun checkStorageAccessPermission() {
        lifecycleScope.launch (Dispatchers.IO) {
            if (cloudAuthReader.list().any { StorageType.LOCAL == it.storageType }) {
                launch (Dispatchers.Main) {
                    if (!storageAccessHelper.hasFullAccess()) {
                        storageAccessHelper.requestFullAccess {}
                    }
                }
            }
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        const val ACTION_SHOW_TASK_STATE: String = "ACTION_SHOW_TASK_STATE"
        const val ACTION_SHOW_SYNC_LOG: String = "ACTION_SHOW_SYNC_LOG"
        const val CODE_OPEN_MAIN_ACTIVITY = 1000

        fun pendingIntentWithAction(context: Context, action: String, arguments: Bundle): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                setAction(action)
                putExtras(arguments)
            }
            return PendingIntent.getActivity(
                context,
                CODE_OPEN_MAIN_ACTIVITY,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}