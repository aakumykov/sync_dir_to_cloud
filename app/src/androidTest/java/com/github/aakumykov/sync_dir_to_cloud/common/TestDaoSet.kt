package com.github.aakumykov.sync_dir_to_cloud.common

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO

object TestDaoSet : DaoSet {

    private var targetContext: Context? = null

    fun get(targetContext: Context): TestDaoSet {
        if (null == this.targetContext)
            this.targetContext = targetContext
        return this
    }

    val appDatabase by lazy {
//        Room.inMemoryDatabaseBuilder(targetContext!!, AppDatabase::class.java).build()
        com.github.aakumykov.sync_dir_to_cloud.appDatabase
    }

    override val cloudAuthDAO: CloudAuthDAO
        get() = appDatabase.getCloudAuthDAO()

    override val syncTaskDAO: SyncTaskDAO
        get() = appDatabase.getSyncTaskDAO()


}

interface DaoSet {
    val cloudAuthDAO: CloudAuthDAO
    val syncTaskDAO: SyncTaskDAO
}