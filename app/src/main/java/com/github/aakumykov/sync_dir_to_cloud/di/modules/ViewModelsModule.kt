package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.app.Application
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ViewModelKey
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit_2.CloudAuthEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_state.TaskStateViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ViewModelsModule {

    @Provides
    @IntoMap
    @ViewModelKey(TaskEditViewModel::class)
    fun provideTaskEditViewModel(application: Application): ViewModel {
        return TaskEditViewModel(application)
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
                                 syncObjectReader: SyncObjectReader,
                                 startStopSyncTaskUseCase: StartStopSyncTaskUseCase
    ): ViewModel
    {
        return TaskStateViewModel(syncTaskReader, syncObjectReader, startStopSyncTaskUseCase)
    }
}