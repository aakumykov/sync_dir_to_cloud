package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.*
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.SyncTaskUpdater
import dagger.Component

@Component(
    modules = [
        ContextModule::class,
        RoomModule::class,
        RepositoryInterfacesModule::class,
        StarterStopperModule::class,
        WorkerModule::class,
        CoroutineModule::class
    ]
)
@AppScope
interface AppComponent {

    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
    fun getStartStopSyncTaskUseCase(): StartStopSyncTaskUseCase

    fun getSyncTaskUpdater(): SyncTaskUpdater
}