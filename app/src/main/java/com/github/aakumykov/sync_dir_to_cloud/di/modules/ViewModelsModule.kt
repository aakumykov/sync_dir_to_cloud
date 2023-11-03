package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.app.Application
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ViewModelKey
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_edit.AuthEditViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.task_edit.TaskEditViewModel
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
}