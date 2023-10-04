package com.github.aakumykov.sync_dir_to_cloud

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.github.aakumykov.sync_dir_to_cloud.room.AppDatabase

class App : Application() {

    companion object {

        private var _appDatabase: AppDatabase? = null

        private fun prepareRoomDatabase(appContext: Context) {
            _appDatabase = Room.databaseBuilder(appContext, AppDatabase::class.java, "app_database")
                .fallbackToDestructiveMigration()
                .build()
        }

        fun getAppDatabase(context: Context) : AppDatabase {
            if (null == _appDatabase)
                prepareRoomDatabase(context)
            return _appDatabase!!
        }
    }
}