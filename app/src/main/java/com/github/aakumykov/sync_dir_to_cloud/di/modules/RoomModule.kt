package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import dagger.Module
import dagger.Provides

@Module
class RoomModule(private val appDatabase: AppDatabase) {

    @Provides
    fun provideSyncTaskDAO(): SyncTaskDAO {
        return appDatabase.getSyncTaskDAO()
    }
}