package com.github.aakumykov.sync_dir_to_cloud.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMain2Binding
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuHelper
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.probe_first_fragment.ProbeFirstFragment
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListFragment

class MainActivity2 : AppCompatActivity(){

    private lateinit var binding: ActivityMain2Binding

    private val menuHelper = MenuHelper(this, R.color.onPrimary, R.color.primary)

    private val navigationViewModel: NavigationViewModel by viewModels()
    private val pageTitleViewModel: PageTitleViewModel by viewModels()
    private val menuStateViewModel: MenuStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prepareLayout()
        prepareViewModels()

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.fragmentContainerView2, TaskListFragment.create(), null)
            .commitNow()
    }

    private fun prepareViewModels() {
        pageTitleViewModel.getPageTitle().observe(this, this::onPageTitleChanged)
        menuStateViewModel.menuState.observe(this, ::onMenuStateChanged)
    }

    private fun prepareLayout() {
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun onPageTitleChanged(title: String?) {
        title?.also {
            binding.toolbar2.title = it
        }
    }

    private fun onMenuStateChanged(menuState: MenuState?) {
        if (null != menuState) {
            with(binding.toolbar2.menu) {
                clear()
                menuHelper.generateMenu(this, menuState.menuItems)
            }
        }
    }
}