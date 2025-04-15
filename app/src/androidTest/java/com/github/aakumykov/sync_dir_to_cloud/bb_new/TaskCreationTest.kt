package com.github.aakumykov.sync_dir_to_cloud.bb_new

import com.github.aakumykov.sync_dir_to_cloud.bb_new.common.SyncTaskTestCase
import org.junit.Assert
import org.junit.Test

class TaskCreationTest : SyncTaskTestCase() {

    @Test
    fun creatingTask() = run {
        createTask()
        checkTaskExists()
    }


    @Test
    fun deletingTask() = run {
        deleteTask()
        checkTaskNotExists()
    }
}