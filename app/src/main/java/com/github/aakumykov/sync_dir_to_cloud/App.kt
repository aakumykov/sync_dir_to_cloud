package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.aakumykov.cloud_reader.di.CloudReadersComponent
import com.github.aakumykov.cloud_reader.di.DaggerCloudReadersComponent
import com.github.aakumykov.sync_dir_to_cloud.config.DbConfig
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.DaggerAppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        prepareAppComponent(this, this)
        prepareAndGetAppDatabase(this)
    }

    companion object {

        // AppComponent
        private var _appComponent: AppComponent? = null

        @Deprecated("заменить на свойство")
        fun getAppComponent(): AppComponent {
            return _appComponent!!
        }

        private fun prepareAppComponent(application: Application, appContext: Context) {
            _appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(appContext))
                .applicationModule(ApplicationModule(application))
                .roomDAOModule(RoomDAOModule(prepareAndGetAppDatabase(appContext)))
                .build()
        }


        // CloudReadersComponent
        private var _cloudReadersComponent: CloudReadersComponent? = null
        val cloudReadersComponent get() = _cloudReadersComponent!!

        private fun prepareCloudReadersComponent() {
            _cloudReadersComponent = DaggerCloudReadersComponent.builder().build()
        }


        // AppDatabase
        private var _appDatabase: AppDatabase? = null

        private fun prepareAndGetAppDatabase(appContext: Context): AppDatabase {
            _appDatabase = Room
                .databaseBuilder(appContext, AppDatabase::class.java, DbConfig.APP_DB_NAME)
                // FIXME: убрать
                .fallbackToDestructiveMigration()
                .build()
            return _appDatabase!!
        }
    }
}