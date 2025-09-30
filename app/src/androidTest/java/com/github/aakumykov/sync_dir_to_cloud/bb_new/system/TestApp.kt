package com.github.aakumykov.sync_dir_to_cloud.bb_new.system

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.DaggerTestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule

class TestApp : App() {

    override fun createComponent(): TestComponent {
        return DaggerTestComponent.builder()
            .contextModule(ContextModule(this))
            .applicationModule(ApplicationModule(this))
            .build()
    }
}