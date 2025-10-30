package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_files.deletion

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class DeletionFlatFilesBase : SyncTestBase() {

    /**
     * Файл в источнике удалён [source_file_was_deleted]
     * Файл в приёмнике удалён [target_file_was_deleted]
     * Файлы в источнике и приёмнике удалены [source_and_target_files_are_deleted]
     */

    open fun source_file_was_deleted() {
        createSourceFileAndSyncItWithTarget()
        fileHelper.deleteFileFromSource(sFileName)
        assertSourceDirChildCount(0)
        doSync()
    }

    open fun target_file_was_deleted() {
        createSourceFileAndSyncItWithTarget()
        fileHelper.deleteFileFromTarget(sFileName)
        assertTargetDirChildCount(0)
        doSync()
    }

    open fun source_and_target_files_are_deleted() {
        createSourceFileAndSyncItWithTarget()
        fileHelper.deleteFileFromSource(sFileName)
        fileHelper.deleteFileFromTarget(sFileName)
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
        doSync()
    }
}