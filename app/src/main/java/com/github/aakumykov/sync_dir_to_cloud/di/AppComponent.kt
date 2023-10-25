package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudAuthRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CoroutineModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker
import dagger.Component

@Component(
    modules = [
        ApplicationModule::class,
        ContextModule::class,
        RoomModule::class,
        SyncTaskRepositoryInterfacesModule::class,
        CloudAuthRepositoryInterfacesModule::class,
        WorkerInterfacesModule::class,
        WorkerModule::class,
        CoroutineModule::class
    ]
)
@AppScope
interface AppComponent {

    // TODO: убрать это
    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
    fun getStartStopSyncTaskUseCase(): StartStopSyncTaskUseCase
    fun getTaskSchedulingUseCase(): SchedulingSyncTaskUseCase

    fun injectSyncTaskWorker(syncTaskWorker: SyncTaskWorker)
    fun injectWorker2(syncTaskWorker: SyncTaskWorker)

    // FIXME: временное
    fun getCloudAuthAdder(): CloudAuthAdder
    fun getCloudAuthLister(): CloudAuthReader
    fun getCloudAuthChecker(): CloudAuthChecker
}