package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.common.test_case.StorageAccessTestCase
import com.github.aakumykov.sync_dir_to_cloud.common.dao_set.TestDaoSet
import com.github.aakumykov.sync_dir_to_cloud.config.file_config.LocalFileCofnig
import com.github.aakumykov.sync_dir_to_cloud.config.task_config.LocalTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.scenario.task.CreateLocalTask
import com.github.aakumykov.sync_dir_to_cloud.scenario.file.creation.CreateSourceFile
import com.github.aakumykov.sync_dir_to_cloud.scenario.task.DeleteLocalTask
import com.github.aakumykov.sync_dir_to_cloud.scenario.RunSync
import com.github.aakumykov.sync_dir_to_cloud.scenario.checks.NoSyncActionsOccurs
import com.github.aakumykov.sync_dir_to_cloud.scenario.checks.SourceAndTargetFilesAreEquals
import com.github.aakumykov.sync_dir_to_cloud.scenario.dir.CreateTargetDir
import com.github.aakumykov.sync_dir_to_cloud.scenario.dir.DeleteTargetDir
import com.github.aakumykov.sync_dir_to_cloud.scenario.file.modification.ModifySourceFile
import com.github.aakumykov.sync_dir_to_cloud.utils.TestFilesManager
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SyncTestsForSyncModeSync : StorageAccessTestCase() {

    private val testDaoSet get() = TestDaoSet.get(device.targetContext)
    private val syncObjectLogDAO get() = testDaoSet.syncObjectLogDAO
    private val testFilesManager: TestFilesManager = TestFilesManager(LocalTaskConfig, LocalFileCofnig)

    @Before
    fun reCreateTask() = run {
        scenario(
            DeleteLocalTask(LocalTaskConfig, testDaoSet)
        )
        scenario(
            CreateLocalTask(LocalTaskConfig, testDaoSet)
        )
    }

    @Before
    fun reCreateTargetDir() = run {
        scenario(
            DeleteTargetDir(LocalTaskConfig)
        )
        scenario(
            CreateTargetDir(LocalTaskConfig)
        )
    }

    // Отсутствует - Отсутствует
    @Test
    fun when_no_files_in_source_and_target_then_no_sync_actions() = run {
        scenario(NoSyncActionsOccurs(testDaoSet))
    }

    // Прежний - Прежний
    @Test
    fun when_both_files_are_unchanged_then_nothing_syncs() = run {
        scenario(CreateSourceFile(LocalFileCofnig))
        scenario(RunSync(LocalTaskConfig)) // Первый прогон копирует файл в приёмник.
        scenario(NoSyncActionsOccurs(testDaoSet))
    }

    // Новый - Отсутствует

    // Изменившийся - Прежний
    @Test
    fun when_new_file_in_source_then_it_synced_to_target() = run {
        scenario(CreateSourceFile(LocalFileCofnig))
        scenario(RunSync(LocalTaskConfig))
        scenario(ModifySourceFile(LocalFileCofnig))
        scenario(RunSync(LocalTaskConfig))
        scenario(SourceAndTargetFilesAreEquals())
    }


    @Test
    fun when_source_file_changed_then_target_file_will_updated() = run {
        scenario(CreateSourceFile(LocalFileCofnig))
        scenario(RunSync(LocalTaskConfig))
        scenario(ModifySourceFile(LocalFileCofnig))
        scenario(RunSync(LocalTaskConfig))

        Assert.assertEquals(
            testFilesManager.sourceFileContents.joinToString(""),
            testFilesManager.targetFileContents.joinToString(""),
        )
    }
}