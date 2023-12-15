package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskStateDAO
import dagger.Module
import dagger.Provides

@Module
class RoomDAOModule(private val appDatabase: AppDatabase) {

    @Provides
    fun provideSyncTaskDAO(): SyncTaskDAO {
        return appDatabase.getSyncTaskDAO()
    }

    @Provides
    fun provideSyncTaskStateDAO(): SyncTaskStateDAO = appDatabase.getSyncTaskStateDAO()

    @Provides
    fun provideCloudAuthDAO(): CloudAuthDAO {
        return appDatabase.getCloudAuthDAO()
    }

    @Provides
    fun provideSyncObjectDAO(): SyncObjectDAO {
        return appDatabase.getSyncObjectDAO()
    }
}