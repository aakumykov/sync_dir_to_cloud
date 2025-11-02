package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.DeepFilesBase
import org.junit.Assert

abstract class DeletionDeepFilesBase : DeepFilesBase() {

    /**
     * Удаление глубокого файла в источнике [deep_file_deletion_in_source]
     * Удаление глубокого файла в приёмнике [deep_file_deletion_in_target]
     * Удаление глубоких файлов в источнике и приёмнике [deep_files_deletion_in_source_and_target]
     */

    open fun deep_file_deletion_in_source() {
        prepareSourceDeepFileAndSyncItWithTarget()

        fileHelper.deleteDeepFileFromSource(sDeepDirName, sFileName)

        assertSourceDirChildCount(1)
        Assert.assertFalse(sDeepFile.exists())

        assertTargetDirChildCount(1)
        Assert.assertTrue(sDeepFileInTarget.exists())

        doSync()
    }

    open fun deep_file_deletion_in_target() {
        prepareSourceDeepFileAndSyncItWithTarget()
        fileHelper.deleteDeepFileFromTarget(sDeepDirName, sFileName)
        doSync()
    }

    open fun deep_files_deletion_in_source_and_target() {
        prepareSourceDeepFileAndSyncItWithTarget()
        fileHelper.deleteDeepFileFromSource(sDeepDirName, sFileName)
        fileHelper.deleteDeepFileFromTarget(sDeepDirName, sFileName)
        doSync()
    }
}