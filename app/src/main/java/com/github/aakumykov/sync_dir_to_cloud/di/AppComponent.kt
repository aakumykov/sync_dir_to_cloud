package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.ViewModelFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_source.StorageToDatabaseLister
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.DirsBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.SyncObjectFileCopierCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.SyncTaskFilesCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter.TaskDirsDeleterCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter.TaskFilesDeleterCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.TaskCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_backuper.DirBackuperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator.DirCreatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter.DirDeleterAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.file_copier.FileCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.SyncObjectCopierAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task.ComparisonsDeleter6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task.InstructionsGeneratorAssistedFactory6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task.SourceWithTargetComparatorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task.SyncInstructionDeleter6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task.SyncInstructionsProcessorAssistedFactory6
import com.github.aakumykov.sync_dir_to_cloud.backuper_restorer.BackuperRestorer
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.AppDatabaseModule
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
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SourceFileStreamSupplierAssistedFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SourceFileStreamSupplierFactoryModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncInstructionRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncObjectLoggerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncObjectRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncOperationLogRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskLoggerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SyncTaskRepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.TaskLoggerModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ViewModelsModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.WorkerModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.cloud_auth.CloudAuthManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth.CloudAuthenticatorFactoryAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogCleaner
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskRunningTimeUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.progress_info_holder.ProgressInfoHolder
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncInstructionRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
import com.github.aakumykov.sync_dir_to_cloud.sync_object_logger.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.SyncTaskExecutorAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.utils.NotificationChannelHelper
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
        SyncObjectLoggerInterfacesModule::class,
        CloudAuthRepositoryInterfacesModule::class,
        SyncObjectRepositoryInterfacesModule::class,
        SyncOperationLogRepositoryInterfacesModule::class,
        ExecutionLogRepositoryInterfacesModule::class,
        SyncInstructionRepositoryInterfacesModule::class,
        WorkerInterfacesModule::class,
        WorkerModule::class,
        CoroutineModule::class,
        ViewModelsModule::class,
        OkhttpModule::class,
        GsonModule::class,
        SourceFileStreamSupplierFactoryModule::class,
        SourceFileStreamSupplierAssistedFactoriesModule::class,
        FileListerCreatorsModule::class,
        CloudReaderFactoriesModule::class,
        CloudWriterFactoriesModule::class,
        TaskLoggerModule::class,
    ]
)
@AppScope
@ExecutionScope
interface AppComponent {

    fun getViewModelFactory(): ViewModelFactory

    // TODO: убрать это
    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
    fun getStartStopSyncTaskUseCase(): StartStopSyncTaskUseCase
    fun getTaskSchedulingUseCase(): SchedulingSyncTaskUseCase

    fun getCloudAuthManagingUseCase(): CloudAuthManagingUseCase

    // FIXME: временное
    fun getCloudAuthAdder(): CloudAuthAdder

    fun getCloudAuthChecker(): CloudAuthChecker

    fun getSyncTaskReader(): SyncTaskReader

    fun getCloudAuthReader(): CloudAuthReader

    fun getSyncTaskExecutorAssistedFactory(): SyncTaskExecutorAssistedFactory

    fun getSyncTaskNotificator(): SyncTaskNotificator

    fun getSyncObjectReader(): SyncObjectReader

    fun getSyncTaskStateChanger(): SyncTaskStateChanger

    fun getSyncTaskRunningTimeUpdater(): SyncTaskRunningTimeUpdater

    fun getSyncTaskStateDAO(): SyncTaskStateDAO

    fun getGson(): Gson

    fun getCloudAuthenticatorFactoryAssistedFactory(): CloudAuthenticatorFactoryAssistedFactory

    fun getSourceFileStreamSupplierCreator(): SourceFileStreamSupplierCreator

    fun getProgressInfoHolder(): ProgressInfoHolder

    fun getStorageToDatabaseLister(): StorageToDatabaseLister

    fun getFileCopierCreator(): SyncObjectFileCopierCreator

    fun getSyncTaskFilesCopierAssistedFactory(): SyncTaskFilesCopierAssistedFactory

    fun getFilesBackuperCreator(): FilesBackuperCreator

    fun getDirsBackuperCreator(): DirsBackuperCreator

    fun getTaskFilesDeleterCreator(): TaskFilesDeleterCreator

    fun getTaskDirDeleterCreator(): TaskDirsDeleterCreator

    fun getSyncObjectLogger(): SyncObjectLogger

    fun getSyncStuff(): SyncStuff

    fun getDirBackuperAssistedFactory(): DirBackuperAssistedFactory
    fun getDirDeleterAssistedFactory(): DirDeleterAssistedFactory
    fun getDirCreatorAssistedFactory(): DirCreatorAssistedFactory

    fun getFileCopierAssistedFactory(): FileCopierAssistedFactory

    fun getOperationCancellationHolder(): OperationCancellationHolder
    fun getTaskCancellationHolder(): TaskCancellationHolder

    fun getExecutionLogCleaner(): ExecutionLogCleaner

    fun getSourceWithTargetComparatorAssistedFactory(): SourceWithTargetComparatorAssistedFactory

    fun getSyncInstructionsProcessorAssistedFactory6(): SyncInstructionsProcessorAssistedFactory6

    fun getSyncObjectCopierAssistedFactory(): SyncObjectCopierAssistedFactory

    fun getInstructionsGeneratorAssistedFactory6(): InstructionsGeneratorAssistedFactory6

    fun getInstructionsDeleter6(): SyncInstructionDeleter6

    fun getComparisonsDeleter6(): ComparisonsDeleter6

    fun getBackuperRestorer(): BackuperRestorer

    fun getNotificationChannelHelper(): NotificationChannelHelper

    fun getSyncObjectDeleter(): SyncObjectDeleter

    fun getSyncInstructionRepository6(): SyncInstructionRepository
}
