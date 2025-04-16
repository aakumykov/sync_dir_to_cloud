package com.github.aakumykov.sync_dir_to_cloud.di.modules

import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.config.DbConfig
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase
import dagger.Module
import dagger.Provides

@Module
class AppDatabaseModule {

    @AppScope
    @Provides
    fun provideAppDatabase(@AppContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                DbConfig.APP_DB_NAME
            ).build()
    }
}