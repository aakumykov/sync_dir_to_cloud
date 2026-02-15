package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationJobIdReader
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository2
import dagger.Binds
import dagger.Module

@Module
abstract class FileOperationLogRepositoryInterfacesModule() {

    @Binds
    abstract fun bindsFileOperationJobIdReader(
        fileOperationLogRepository2: FileOperationLogRepository2
    ): FileOperationJobIdReader
}
