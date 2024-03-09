package com.github.aakumykov.sync_dir_to_cloud.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.databinding.ActivityMain2Binding
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuHelper
import com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper.MenuState
import com.github.aakumykov.sync_dir_to_cloud.view.probe_first_fragment.ProbeFirstFragment

class MainActivity2 : AppCompatActivity(){

    private lateinit var binding: ActivityMain2Binding
    private val menuHelper = MenuHelper(this, R.color.onPrimary, R.color.primary)
    private val menuStateViewModel: MenuStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        menuStateViewModel.menuState.observe(this, ::onMenuStateChanged)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView2, ProbeFirstFragment.create(), null)
            .commitNow()
    }

    private fun onMenuStateChanged(menuState: MenuState?) {
        if (null != menuState) {
            menuHelper.generateMenu(binding.toolbar2.menu, menuState.menuItems)
        }
    }
}