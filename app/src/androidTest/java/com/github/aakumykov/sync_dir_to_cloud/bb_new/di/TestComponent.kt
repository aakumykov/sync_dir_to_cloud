package com.github.aakumykov.sync_dir_to_cloud.bb_new.di

import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestDaoModule
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import dagger.Component

@Component(
    modules = [
        TestContextModule::class,
        TestDaoModule::class,
    ]
)
abstract class TestComponent : AppComponent {


}