package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.ViewModelFactory
import com.github.aakumykov.sync_dir_to_cloud.app_settings.AppSettings
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.AppDatabaseModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.AppSettingsModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudAuthRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudReaderFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CloudWriterFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.CoroutineModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ExecutionLogRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.FileListerCreatorsModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.GsonModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.NotificationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.OkhttpModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.PreferencesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ResourcesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SharedPreferencesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncInstructionRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncObjectRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskLoggerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.TaskLoggerModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.TempModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ViewModelsModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.cloud_auth.CloudAuthManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth.CloudAuthenticatorFactoryAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.progress_info_holder.ProgressInfoHolder
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_backuper_restorer.BackuperRestorer
import com.github.aakumykov.sync_dir_to_cloud.sync_task_processor.SyncTaskProcessorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.workers.SyncTaskWorker
import com.google.gson.Gson
import dagger.Component

@Component(
    modules = [
        ApplicationModule::class,
        ContextModule::class,
        ResourcesModule::class,
        NotificationModule::class,
        AppDatabaseModule::class,
        RoomDAOModule::class,
        SyncTaskRepositoryInterfacesModule::class,
        SyncTaskLoggerInterfacesModule::class,
        CloudAuthRepositoryInterfacesModule::class,
        SyncObjectRepositoryInterfacesModule::class,
        ExecutionLogRepositoryInterfacesModule::class,
        SyncInstructionRepositoryInterfacesModule::class,
        WorkerInterfacesModule::class,
        WorkerModule::class,
        CoroutineModule::class,
        ViewModelsModule::class,
        OkhttpModule::class,
        GsonModule::class,
        FileListerCreatorsModule::class,
        CloudReaderFactoriesModule::class,
        CloudWriterFactoriesModule::class,
        TaskLoggerModule::class,
        PreferencesModule::class,
        SharedPreferencesModule::class,
        AppSettingsModule::class,
        TempModule::class
    ]
)
@AppScope
@ExecutionScope
interface AppComponent {

    fun injectToSyncTaskWorker(syncTaskWorker: SyncTaskWorker)

    fun getViewModelFactory(): ViewModelFactory

    fun getCloudAuthManagingUseCase(): CloudAuthManagingUseCase

    fun getCloudAuthReader(): CloudAuthReader

    fun getSyncTaskProcessorAssistedFactory(): SyncTaskProcessorAssistedFactory

    fun getSyncTaskRunningTimeUpdater(): SyncTaskRunningTimeUpdater

    fun getSyncTaskStateDAO(): SyncTaskStateDAO

    fun getGson(): Gson

    fun getCloudAuthenticatorFactoryAssistedFactory(): CloudAuthenticatorFactoryAssistedFactory

    fun getProgressInfoHolder(): ProgressInfoHolder

    fun getOperationCancellationHolder(): OperationCancellationHolder

    fun getBackuperRestorer(): BackuperRestorer

    fun getAppSettings(): AppSettings
}
