package com.github.aakumykov.sync_dir_to_cloud.bb_new.di.modules

import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.bb_new.room.TestDatabase
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class TestDatabaseModule {

    @AppScope
    @Provides
    fun provideTestDatabase(@AppContext appContext: Context): TestDatabase {
        return Room.inMemoryDatabaseBuilder(
            appContext,
            TestDatabase::class.java
        ).build()
    }


    @AppScope
    @Provides
    fun provideAppDatabase(testDatabase: TestDatabase): AppDatabase {
        return testDatabase as AppDatabase
    }
}