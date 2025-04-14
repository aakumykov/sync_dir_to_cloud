package com.github.aakumykov.sync_dir_to_cloud.bb_new.di

import com.github.aakumykov.sync_dir_to_cloud.bb_new.InstrumentedTest1
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestDaoModule
import com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules.TestContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.ResourcesModule
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
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
import com.github.aakumykov.sync_dir_to_cloud.di.modules.StorageReaderAssistedFactoriesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.StorageWriterFactoriesModule
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
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriters2_Module
import dagger.Component

@Component(
    modules = [
        TestContextModule::class,
        TestDaoModule::class,

        ApplicationModule::class,
        ResourcesModule::class,
        NotificationModule::class,
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
        StorageReaderAssistedFactoriesModule::class,
        OkhttpModule::class,
        GsonModule::class,
        SourceFileStreamSupplierFactoryModule::class,
        SourceFileStreamSupplierAssistedFactoriesModule::class,
        FileListerCreatorsModule::class,
        CloudReaderFactoriesModule::class,
        CloudWriterFactoriesModule::class,
        StorageWriterFactoriesModule::class,
        StorageWriters2_Module::class,
        TaskLoggerModule::class,
    ]
)
@AppScope
@ExecutionScope
interface TestComponent : AppComponent {
    fun injectInstrumentedTest1(instrumentedTest1: InstrumentedTest1)
}