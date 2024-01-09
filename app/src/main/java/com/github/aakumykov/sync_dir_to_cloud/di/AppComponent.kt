package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.ViewModelFactory
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.FileListerAssistedFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudAuthRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudWriterAssistedFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CoroutineModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.GsonModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.NotificationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.OkhttpModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SourceReaderAssistedFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncObjectRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.TargetWriterAssistedFactoriesModule3
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ViewModelsModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.cloud_auth.CloudAuthManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateReader
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutor
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskNotificator
import dagger.Component

@Component(
    modules = [
        ApplicationModule::class,
        ContextModule::class,
        NotificationModule::class,
        RoomDAOModule::class,
        SyncTaskRepositoryInterfacesModule::class,
        CloudAuthRepositoryInterfacesModule::class,
        SyncObjectRepositoryInterfacesModule::class,
        WorkerInterfacesModule::class,
        WorkerModule::class,
        CoroutineModule::class,
        ViewModelsModule::class,
        FileListerAssistedFactoriesModule::class,
        SourceReaderAssistedFactoriesModule::class,
        CloudWriterAssistedFactoriesModule::class,
        TargetWriterAssistedFactoriesModule3::class,
        OkhttpModule::class,
        GsonModule::class
    ]
)
@AppScope
interface AppComponent {

    fun getViewModelFactory(): ViewModelFactory

    // TODO: убрать это
    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
    fun getStartStopSyncTaskUseCase(): StartStopSyncTaskUseCase
    fun getTaskSchedulingUseCase(): SchedulingSyncTaskUseCase

    fun getCloudAuthManagingUseCase(): CloudAuthManagingUseCase

    // FIXME: временное
    fun getCloudAuthAdder(): CloudAuthAdder
    fun getCloudAuthLister(): CloudAuthReader
    fun getCloudAuthChecker(): CloudAuthChecker

    fun getSyncTaskReader(): SyncTaskReader

    fun getCloudAuthReader(): CloudAuthReader

    fun getSyncTaskExecutor(): SyncTaskExecutor

    fun getSyncTaskStateReader(): SyncTaskStateReader

    fun getSyncTaskNotificator(): SyncTaskNotificator
}