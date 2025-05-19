package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncOperationLoggerDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskBackupDirDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import dagger.Module
import dagger.Provides

@Module
class RoomDAOModule {

    @Provides
    fun provideSyncTaskDAO(appDatabase: AppDatabase): SyncTaskDAO {
        return appDatabase.getSyncTaskDAO()
    }

    @Provides
    fun provideSyncTaskStateDAO(appDatabase: AppDatabase): SyncTaskStateDAO = appDatabase.getSyncTaskStateDAO()

    @Provides
    fun provideSyncTaskRunningTimeDAO(appDatabase: AppDatabase): SyncTaskRunningTimeDAO = appDatabase.getSyncTaskRunningTimeDAO()

    @Provides
    fun provideSyncTaskSchedulingStateDAO(appDatabase: AppDatabase): SyncTaskSchedulingStateDAO = appDatabase.getSyncTaskSchedulingStateDAO()

    @Provides
    fun provideSyncTaskBackupDirDAO(appDatabase: AppDatabase): SyncTaskBackupDirDAO = appDatabase.getSyncTaskBackupDirDAO()

    @Provides
    fun provideSyncTaskExecutionStateDAO(appDatabase: AppDatabase): SyncTaskSyncStateDAO = appDatabase.getSyncTaskExecutionStateDAO()

    @Provides
    fun provideCloudAuthDAO(appDatabase: AppDatabase): CloudAuthDAO {
        return appDatabase.getCloudAuthDAO()
    }

    @Provides
    fun provideSyncObjectDAO(appDatabase: AppDatabase): SyncObjectDAO {
        return appDatabase.getSyncObjectDAO()
    }

    @Provides
    fun provideSyncTaskResettingDAO(appDatabase: AppDatabase): SyncTaskResettingDAO {
        return appDatabase.getSyncTaskResettingDAO()
    }

    @Provides
    fun provideSyncObjectStateDAO(appDatabase: AppDatabase): SyncObjectStateSetterDAO {
        return appDatabase.getSyncObjectStateDAO()
    }

    @Provides
    fun provideSyncObjectStateResettingDAO(appDatabase: AppDatabase): SyncObjectBadStateResettingDAO {
        return appDatabase.getSyncObjectBadStateResettingDAO()
    }

    @Provides
    fun provideSyncObjectResettingDAO(appDatabase: AppDatabase): BadObjectStateResettingDAO {
        return appDatabase.getSyncObjectResettingDAO()
    }

    @Provides
    fun provideTaskLogDAO(appDatabase: AppDatabase): SyncTaskLogDAO {
        return appDatabase.getTaskLogDAO()
    }

    @Provides
    fun provideSyncObjectLogDAO(appDatabase: AppDatabase): SyncObjectLogDAO {
        return appDatabase.getSyncObjectLogDAO()
    }

    @Provides
    fun provideExecutionLogDAO(appDatabase: AppDatabase): ExecutionLogDAO {
        return appDatabase.getExecutionLogDAO()
    }

    @Provides
    fun provideComparisonStateDAO(appDatabase: AppDatabase): ComparisonStateDAO {
        return appDatabase.getComparisonStateDAO()
    }

    @Provides
    fun provideSyncInstructionDAO6(appDatabase: AppDatabase): SyncInstructionDAO {
        return appDatabase.getSyncInstructionDAO6()
    }

    @Provides
    fun provideSyncOperationLoggerDAO(appDatabase: AppDatabase): SyncOperationLoggerDAO {
        return appDatabase.getSyncOperationLoggerDAO()
    }
}