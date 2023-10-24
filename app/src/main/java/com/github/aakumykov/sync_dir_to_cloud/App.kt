package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.config.DbConfig
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.DaggerAppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomModule
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        prepareAppComponent(this, this)
        prepareAndGetAppDatabase(this)
    }

    companion object {

        private var _appComponent: AppComponent? = null
        private var _appDatabase: AppDatabase? = null

        fun getAppComponent(): AppComponent {
            return _appComponent!!
        }

        fun appDatabase(): AppDatabase {
            return _appDatabase!!
        }

        fun prepareAppComponent(application: Application, appContext: Context) {
            _appComponent = DaggerAppComponent.builder()
                .contextModule(ContextModule(appContext))
                .applicationModule(ApplicationModule(application))
                .roomModule(RoomModule(prepareAndGetAppDatabase(appContext)))
                .build()
        }

        fun prepareAndGetAppDatabase(appContext: Context): AppDatabase {
            _appDatabase = Room
                .databaseBuilder(appContext, AppDatabase::class.java, DbConfig.APP_DB_NAME)
                // FIXME: убрать
                .fallbackToDestructiveMigration()
                .build()
            return _appDatabase!!
        }
    }
}