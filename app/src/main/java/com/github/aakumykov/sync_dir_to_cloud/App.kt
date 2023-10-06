package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.config.DbConfig
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.DaggerAppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.SystemModule
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase

class App : Application() {

    private val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .systemModule(SystemModule(this))
            .roomModule(RoomModule(appDatabase))
            .build()
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            DbConfig.APP_DB_NAME
        ).build()
    }

    companion object {
        fun getAppComponent(application: Application): AppComponent {
            return (application as App).appComponent
        }
        fun getAppDatabase(application: Application) : AppDatabase {
            return (application as App).appDatabase
        }
    }
}