package com.github.aakumykov.sync_dir_to_cloud.scenario.aa_di_probe

import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import dagger.Module
import dagger.Provides

@Module
class TestDaoModule(private val appDatabase: AppDatabase) {

    @Provides
    fun provideSyncTaskDAO(): SyncTaskDAO {
        return appDatabase.getSyncTaskDAO()
    }

    @Provides
    fun provideSyncObjectDAO(): SyncObjectDAO {
        return appDatabase.getSyncObjectDAO()
    }

    @Provides
    fun provideSyncObjectLogDAO(): SyncObjectLogDAO {
        return appDatabase.getSyncObjectLogDAO()
    }
}