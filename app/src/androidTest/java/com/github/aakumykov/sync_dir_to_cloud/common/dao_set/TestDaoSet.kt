package com.github.aakumykov.sync_dir_to_cloud.common.dao_set

import android.content.Context
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.CloudAuthDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskDAO

object TestDaoSet : DaoSet {

    private var targetContext: Context? = null

    fun get(targetContext: Context): TestDaoSet {
        if (null == TestDaoSet.targetContext)
            TestDaoSet.targetContext = targetContext
        return this
    }

    val testAppDatabase by lazy {
//        Room.inMemoryDatabaseBuilder(targetContext!!, AppDatabase::class.java).build()
        com.github.aakumykov.sync_dir_to_cloud.appDatabase
    }

    override val cloudAuthDAO: CloudAuthDAO
        get() = testAppDatabase.getCloudAuthDAO()

    override val syncTaskDAO: SyncTaskDAO
        get() = testAppDatabase.getSyncTaskDAO()

    override val syncObjectLogDAO: SyncObjectLogDAO
        get() = testAppDatabase.getSyncObjectLogDAO()
}

