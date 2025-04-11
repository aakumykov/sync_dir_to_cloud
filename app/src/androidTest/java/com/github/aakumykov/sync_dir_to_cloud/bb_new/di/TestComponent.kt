package com.github.aakumykov.sync_dir_to_cloud.bb_new.di

import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestDaoModule
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudAuthRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.GsonModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncObjectRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskLoggerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskRepositoryInterfacesModule
import dagger.Component

@Component(
    modules = [
        TestContextModule::class,
        TestDaoModule::class,
        CloudAuthRepositoryInterfacesModule::class,
        SyncTaskRepositoryInterfacesModule::class,
        SyncObjectRepositoryInterfacesModule::class,
        RoomDAOModule::class,
        GsonModule::class,
    ]
)
@AppScope
@ExecutionScope
abstract class TestComponent : AppComponent {

}