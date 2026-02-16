package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.ComparisonStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.FileOperationLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.InstructionLoggingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.LogOfSyncDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncInstructionDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskBackupDirDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskRunningTimeDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSchedulingStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskSyncStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskLogger2DAO
import dagger.Module
import dagger.Provides

@Module
class RoomDAOModule {

    @Provides
    fun provideSyncTaskDAO(appDatabase: AppDatabase): SyncTaskDAO = appDatabase.getSyncTaskDAO()

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
    fun provideCloudAuthDAO(appDatabase: AppDatabase): CloudAuthDAO = appDatabase.getCloudAuthDAO()

    @Provides
    fun provideSyncObjectDAO(appDatabase: AppDatabase): SyncObjectDAO = appDatabase.getSyncObjectDAO()

    @Provides
    fun provideSyncTaskResettingDAO(appDatabase: AppDatabase): SyncTaskResettingDAO = appDatabase.getSyncTaskResettingDAO()

    @Provides
    fun provideSyncObjectStateDAO(appDatabase: AppDatabase): SyncObjectStateSetterDAO = appDatabase.getSyncObjectStateDAO()

    @Provides
    fun provideSyncObjectStateResettingDAO(appDatabase: AppDatabase): SyncObjectBadStateResettingDAO = appDatabase.getSyncObjectBadStateResettingDAO()

    @Provides
    fun provideTaskLogDAO(appDatabase: AppDatabase): SyncTaskLogDAO = appDatabase.getTaskLogDAO()


    @Provides
    fun provideComparisonStateDAO(appDatabase: AppDatabase): ComparisonStateDAO = appDatabase.getComparisonStateDAO()

    @Provides
    fun provideSyncInstructionDAO6(appDatabase: AppDatabase): SyncInstructionDAO = appDatabase.getSyncInstructionDAO6()

    @Provides
    fun provideTaskLoggerDAO2(appDatabase: AppDatabase): TaskLogger2DAO = appDatabase.getTaskLogger2DAO()

    @Provides
    fun provideInstructionLoggingDAO(appDatabase: AppDatabase): InstructionLoggingDAO = appDatabase.getInstructionLoggingDAO()

    @Provides
    fun provideFileOperationLogDAO(appDatabase: AppDatabase): FileOperationLogDAO = appDatabase.getFileOperationLogDAO()

    @Provides
    fun provideLogOfSyncDAO(appDatabase: AppDatabase): LogOfSyncDAO = appDatabase.getLogOfSyncDAO()
}