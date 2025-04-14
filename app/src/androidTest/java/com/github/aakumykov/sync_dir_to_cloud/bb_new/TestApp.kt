package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.DaggerTestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.TestComponent
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule

class TestApp : App() {

    override fun createComponent(): TestComponent {
        return DaggerTestComponent.builder()
            .testContextModule(TestContextModule(this))
            .testApplicationModule(TestApplicationModule(this))
            .roomDAOModule(RoomDAOModule(prepareAndGetAppDatabase(this.applicationContext)))
            .build()
    }
}