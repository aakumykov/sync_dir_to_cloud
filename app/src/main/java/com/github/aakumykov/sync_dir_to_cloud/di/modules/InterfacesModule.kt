package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.CloudAuthSetter
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditViewModel
import dagger.Module
import dagger.Provides

@Module
class InterfacesModule {

    @Provides
    fun provideCloudAuthSetter(taskEditViewModel: TaskEditViewModel): CloudAuthSetter {
        return taskEditViewModel
    }
}