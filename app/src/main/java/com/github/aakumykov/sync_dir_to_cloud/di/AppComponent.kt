package com.github.aakumykov.sync_dir_to_cloud.di

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RepositoryInterfacesModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomModule
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.SyncTaskManagingUseCase
import dagger.Component

@Component(
    modules = [
        ContextModule::class,
        RoomModule::class,
        RepositoryInterfacesModule::class
    ]
)
@AppScope
interface AppComponent {

    fun getSyncTaskManagingUseCase(): SyncTaskManagingUseCase
}