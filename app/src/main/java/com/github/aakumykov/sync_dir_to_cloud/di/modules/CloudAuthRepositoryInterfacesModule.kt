package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthChecker
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.repository.CloudAuthRepository
import dagger.Module
import dagger.Provides

@Module
class CloudAuthRepositoryInterfacesModule {

    @Provides
    fun provideCloudAuthAdder(cloudAuthRepository: CloudAuthRepository): CloudAuthAdder
        = cloudAuthRepository

    @Provides
    fun provideCloudAuthLister(cloudAuthRepository: CloudAuthRepository): CloudAuthReader
        = cloudAuthRepository

    @Provides
    fun provideCloudAuthChecker(cloudAuthRepository: CloudAuthRepository): CloudAuthChecker
        = cloudAuthRepository
}