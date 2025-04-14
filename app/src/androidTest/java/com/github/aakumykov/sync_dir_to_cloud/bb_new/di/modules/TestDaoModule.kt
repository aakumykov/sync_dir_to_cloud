package com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules

import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestDatabase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.dao.TestSyncTaskDAO
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import dagger.Module
import dagger.Provides

@Module
class TestDaoModule() {

    @AppScope
    @Provides
    fun provideTestSyncTaskDAO(@AppScope testDatabase: TestDatabase): TestSyncTaskDAO {
        return testDatabase.getTestSyncTaskDAO()
    }
}