package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
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
    fun provideSyncTaskRunningTimeDAO(): SyncTaskRunningTimeDAO = appDatabase.getSyncTaskRunningTimeDAO()

    @Provides
    fun provideSyncTaskSchedulingStateDAO(): SyncTaskSchedulingStateDAO = appDatabase.getSyncTaskSchedulingStateDAO()

    @Provides
    fun provideSyncTaskExecutionStateDAO(): SyncTaskSyncStateDAO = appDatabase.getSyncTaskExecutionStateDAO()

    @Provides
    fun provideCloudAuthDAO(): CloudAuthDAO {
        return appDatabase.getCloudAuthDAO()
    }

    @Provides
    fun provideSyncObjectDAO(): SyncObjectDAO {
        return appDatabase.getSyncObjectDAO()
    }

    @Provides
    fun provideSyncObjectStateDAO(): SyncObjectStateDAO {
        return appDatabase.getSyncObjectStateDAO()
    }

    @Provides
    fun provideSyncObjectStateResettingDAO(): SyncObjectBadStateResettingDAO {
        return appDatabase.getSyncObjectBadStateResettingDAO()
    }

    @Provides
    fun provideSyncObjectResettingDAO(): BadObjectStateResettingDAO {
        return appDatabase.getSyncObjectResettingDAO()
    }
}