package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class CreationDeepFilesBase : SyncTestBase() {

    // TODO: проверять, что в глубоких каталогах нет лишнего

    /**
     * Глубокий файл создаётся в источнике [deep_file_created_in_source]
     * Глубокий файл создаётся в приёмнике [deep_file_created_in_target]
     *
     * Одноимённые одинаковые глубокие файлы создаются в источнике и приёмнике [same_name_same_content_deep_files_created_in_source_and_target]
     * Одноимённые разные файлы создаются в источнике и приёмнике [same_name_diff_content_deep_files_created_in_source_and_target]
     *
     * Разноимённые файлы создаются в источнике и приёмнике [diff_name_deep_files_created_in_source_and_target]
     */

    open fun deep_file_created_in_source() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)
        assertExistsAndContains(sDeepFile, sFileData)
        doSync()
    }

    open fun deep_file_created_in_target() {
        fileHelper.createDeepFileInTarget(tDeepDirName, tFileName, tFileData)
        assertExistsAndContains(tDeepFile, tFileData)
        doSync()
    }

    open fun same_name_same_content_deep_files_created_in_source_and_target() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, sFileData)
        assertExistsAndContains(sDeepFile, sFileData)
        assertExistsAndContains(sDeepFileInTarget, sFileData)
        doSync()
    }

    open fun same_name_diff_content_deep_files_created_in_source_and_target() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, tFileData)
        assertExistsAndContains(sDeepFile, sFileData)
        assertExistsAndContains(sDeepFileInTarget, tFileData)
        doSync()
    }

    open fun diff_name_deep_files_created_in_source_and_target() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)
        fileHelper.createDeepFileInTarget(tDeepDirName, tFileName, tFileData)
        assertExistsAndContains(sDeepFile, sFileData)
        assertExistsAndContains(tDeepFile, tFileData)
        doSync()
    }
}