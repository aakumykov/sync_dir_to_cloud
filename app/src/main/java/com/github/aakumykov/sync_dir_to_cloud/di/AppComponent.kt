package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.StarterStopperModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SystemModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import dagger.Component

@Component(
    modules = [
        SystemModule::class,
        RoomModule::class,
        RepositoryInterfacesModule::class,
        StarterStopperModule::class
    ]
)
@AppScope
interface AppComponent {

    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
    fun getStartStopSyncTaskUseCase(): StartStopSyncTaskUseCase
}