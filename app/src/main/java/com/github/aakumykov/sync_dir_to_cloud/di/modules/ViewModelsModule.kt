package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.app.Application
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.TaskCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ViewModelKey
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SchedulingSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskStateLogger
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.SyncOperationLogReader
import com.github.aakumykov.sync_dir_to_cloud.notificator.SyncTaskNotificator
import com.github.aakumykov.sync_dir_to_cloud.view.MenuStateViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2.CloudAuthEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.PageTitleViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.navigation.NavigationViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_list.TaskListViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_state.TaskStateViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ViewModelsModule {

    @Provides
    @IntoMap
    @ViewModelKey(TaskEditViewModel::class)
    fun provideTaskEditViewModel(
        application: Application,
        cloudAuthReader: CloudAuthReader,
        syncTaskManagingUseCase: SyncTaskManagingUseCase,
        syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
    ): ViewModel {
        return TaskEditViewModel(application,
            syncTaskManagingUseCase,
            syncTaskSchedulingUseCase,
            cloudAuthReader)
    }

    @Provides
    @IntoMap
    @ViewModelKey(AuthEditViewModel::class)
    fun provideAuthEditViewModel(application: Application): ViewModel {
        return AuthEditViewModel(application)
    }

    @Provides
    @IntoMap
    @ViewModelKey(CloudAuthEditViewModel::class)
    fun provideCloudAuthEditViewModel(cloudAuthAdder: CloudAuthAdder): ViewModel {
        return CloudAuthEditViewModel(cloudAuthAdder)
    }

    @Provides
    @IntoMap
    @ViewModelKey(TaskStateViewModel::class)
    fun provideTaskInfoViewModel(syncTaskReader: SyncTaskReader,
                                 syncObjectDBReader: SyncObjectDBReader,
                                 startStopSyncTaskUseCase: StartStopSyncTaskUseCase,
                                 taskLogRepository: TaskLogRepository,
                                 taskStateLogger: TaskStateLogger,
    ): ViewModel
    {
        return TaskStateViewModel(
            syncTaskReader = syncTaskReader,
            syncObjectDBReader = syncObjectDBReader,
            startStopSyncTaskUseCase = startStopSyncTaskUseCase,
            taskLogRepository = taskLogRepository,
            taskStateLogger = taskStateLogger,
        )
    }

    @Provides
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    fun provideNavigationViewModel(): ViewModel {
        return NavigationViewModel()
    }

    @Provides
    @IntoMap
    @ViewModelKey(PageTitleViewModel::class)
    fun providePageTitleViewModel(): ViewModel {
        return PageTitleViewModel()
    }

    @Provides
    @IntoMap
    @ViewModelKey(MenuStateViewModel::class)
    fun provideMenuStateViewModel(): ViewModel {
        return MenuStateViewModel()
    }

    @Provides
    @IntoMap
    @ViewModelKey(TaskListViewModel::class)
    fun provideTaskListViewModel(application: Application,
                                 syncTaskManagingUseCase: SyncTaskManagingUseCase,
                                 syncTaskStartStopUseCase: StartStopSyncTaskUseCase,
                                 syncTaskSchedulingUseCase: SchedulingSyncTaskUseCase,
                                 syncTaskNotificator: SyncTaskNotificator,
                                 syncObjectDBDeleter: SyncObjectDBDeleter,
                                 taskCancellationHolder: TaskCancellationHolder,
    ): ViewModel {
        return TaskListViewModel(
            application = application,
            syncTaskManagingUseCase =  syncTaskManagingUseCase,
            syncTaskStartStopUseCase = syncTaskStartStopUseCase,
            syncTaskSchedulingUseCase = syncTaskSchedulingUseCase,
            syncTaskNotificator = syncTaskNotificator,
            syncObjectDBDeleter = syncObjectDBDeleter,
            taskCancellationHolder = taskCancellationHolder,
        )
    }

    @Provides
    @IntoMap
    @ViewModelKey(SyncLogViewModel::class)
    fun provideSyncLogViewModel(
        syncOperationLogReader: SyncOperationLogReader,
        executionLogReader: ExecutionLogReader,
        operationCancellationHolder: OperationCancellationHolder
    ): ViewModel {
        return SyncLogViewModel(
            syncOperationLogReader,
            executionLogReader,
            operationCancellationHolder
        )
    }
}