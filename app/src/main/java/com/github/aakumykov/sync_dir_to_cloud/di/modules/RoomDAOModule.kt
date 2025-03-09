package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO6
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ExecutionLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO5
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
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
    fun provideSyncTaskResettingDAO(): SyncTaskResettingDAO {
        return appDatabase.getSyncTaskResettingDAO()
    }

    @Provides
    fun provideSyncObjectStateDAO(): SyncObjectStateSetterDAO {
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

    @Provides
    fun provideTaskLogDAO(): SyncTaskLogDAO {
        return appDatabase.getTaskLogDAO()
    }

    @Provides
    fun provideSyncObjectLogDAO(): SyncObjectLogDAO {
        return appDatabase.getSyncObjectLogDAO()
    }

    @Provides
    fun provideExecutionLogDAO(): ExecutionLogDAO {
        return appDatabase.getExecutionLogDAO()
    }

    @Provides
    fun provideSyncInstructionDAO(): SyncInstructionDAO {
        return appDatabase.getSyncInstructionDAO()
    }

    @Provides
    fun provideSyncInstructionDAO5(): SyncInstructionDAO5 {
        return appDatabase.getSyncInstructionDAO5()
    }

    @Provides
    fun provideComparisonStateDAO(): ComparisonStateDAO {
        return appDatabase.getComparisonStateDAO()
    }

    @Provides
    fun provideSyncInstructionDAO6(): SyncInstructionDAO6 {
        return appDatabase.getSyncInstructionDAO6()
    }
}