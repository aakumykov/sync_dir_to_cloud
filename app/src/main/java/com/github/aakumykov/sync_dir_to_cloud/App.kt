package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.config.DbConfig
import com.github.aakumykov.sync_dir_to_cloud.di.AppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.DaggerAppComponent
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ApplicationModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.ContextModule
import com.github.aakumykov.sync_dir_to_cloud.di.modules.RoomDAOModule
import com.github.aakumykov.sync_dir_to_cloud.repository.room.AppDatabase

open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        _appComponent = createComponent()
    }

    companion object {

        private var _appComponent: AppComponent? = null

        fun getAppComponent(): AppComponent {
            return _appComponent!!
        }
    }

    open fun createComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .contextModule(ContextModule(this.applicationContext))
            .applicationModule(ApplicationModule(this))
            .build()
    }

    fun component(): AppComponent {
        return App.getAppComponent()
    }
}

val appComponent: AppComponent get() = App.getAppComponent()
