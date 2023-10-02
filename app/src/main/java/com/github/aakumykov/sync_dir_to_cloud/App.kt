package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.room.AppDatabase

class App : Application() {

    val appDatabase by lazy {
        prepareRoomDatabase(this)
    }

    private fun prepareRoomDatabase(appContext: Context): AppDatabase {
        return Room.databaseBuilder(appContext, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    /*companion object {

        fun getAppDatabase(appContext: Context): AppDatabase {
            if (null == appDatabase) {
                appDatabase = prepareRoomDatabase(appContext)
                return appDatabase
            }
            return appDatabase
        }


    }*/
}