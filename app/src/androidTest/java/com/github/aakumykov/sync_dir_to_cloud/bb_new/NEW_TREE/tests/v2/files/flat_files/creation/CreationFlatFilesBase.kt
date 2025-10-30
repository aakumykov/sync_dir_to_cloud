package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class CreationFlatFilesBase : SyncTestBase() {

    /**
     * ==== sync ====>
     *
     * Нет файлов в источнике и приёмнике [no_files_in_source_and_target]
     *
     * В источнике появляется файл [file_created_in_source]
     * В приёмнике появляется файл [file_created_in_target]
     *
     * Фйлы появляются в источнике и приёмнике [files_created_in_source_and_target]
     */

    open fun no_files_in_source_and_target() {
        doSync()
    }

    open fun file_created_in_source() {
        fileHelper.createFileInSource(sFileName, sFileData)
        doSync()
    }

    open fun file_created_in_target() {
        fileHelper.createFileInTarget(tFileName, tFileData)
        doSync()
    }

    open fun files_created_in_source_and_target() {
        fileHelper.createFileInSource(sFileName, sFileData)
        fileHelper.createFileInTarget(tFileName, tFileData)
        doSync()
    }
}