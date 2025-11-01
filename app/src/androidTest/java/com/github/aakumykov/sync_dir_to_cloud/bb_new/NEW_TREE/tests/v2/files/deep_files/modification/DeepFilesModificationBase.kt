package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.deep_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import java.util.concurrent.TimeUnit

abstract class DeepFilesModificationBase : SyncTestBase() {

    /**
     * Файл в источнике изменился по времени [source_deep_file_changed_by_time]
     * Файл в источнике изменился в размере [source_deep_file_changed_by_size]
     *
     * Файл в приёмнике изменился по времени [target_deep_file_changed_by_time]
     * Файл в приёмнике изменился в размере [deep_file_in_target_changed_by_size]
     *
     * Файлы в источнике и приёмнике изменились по времени [both_deep_files_are_changed_by_time]
     * Файлы в источнике и приёмнике изменились в размере [both_deep_files_are_changed_by_size]
     *
     * Файл в источнике изменился по времени, а в приёмнике в размере [file_changed_by_time_in_source_and_by_size_in_target]
     * Файл в источнике изменился в размере, а в приёмнике по времени [source_deep_file_changed_by_size_and_target_by_time]
     */


    // ==== sync ====>
    open fun source_deep_file_changed_by_time() {
        prepareSourceDeepFileAndSyncItWithTarget()
        sleep()
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newSourceFileData)
        doSync()
    }


    // ==== sync ====>
    open fun source_deep_file_changed_by_size() {
        prepareSourceDeepFileAndSyncItWithTarget()
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newBigSourceFileData)
        doSync()
    }


    // ==== sync ====>
    open fun target_deep_file_changed_by_time() {
        prepareSourceDeepFileAndSyncItWithTarget()
        sleep()
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newTargetFileData)
        doSync()
    }


    // ==== sync ====>
    open fun deep_file_in_target_changed_by_size() {
        prepareSourceDeepFileAndSyncItWithTarget()
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newBigTargetFileData)
        doSync()
    }


    // ==== sync ====>
    open fun both_deep_files_are_changed_by_time() {
        prepareSourceDeepFileAndSyncItWithTarget()
        // Для изменения файлов использую различающиеся данные, хотя их изменение
        // и определяется не относительно друг друга, а относительно их собственного
        // прошлого состояния. Разные данные нужны для определения того, что данные стали
        // одинаковыми именно в результате синхронизации.
        sleep()
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newSourceFileData)
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newTargetFileData)
        doSync()
    }


    // ==== sync ====>
    open fun both_deep_files_are_changed_by_size() {
        prepareSourceDeepFileAndSyncItWithTarget()
        // Для изменения файлов использую различающиеся данные, хотя их изменение
        // и определяется не относительно друг друга, а относительно их собственного
        // прошлого состояния. Разные данные нужны для определения того, что данные стали
        // одинаковыми именно в результате синхронизации.
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newBigSourceFileData)
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newBigTargetFileData)
        doSync()
    }


    // ==== sync ====>
    open fun file_changed_by_time_in_source_and_by_size_in_target() {
        prepareSourceDeepFileAndSyncItWithTarget()

        // Меняю в приёмнике тот файл, что был синхронизирован из Источника.
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newBigTargetFileData)

        // После паузы меняю файл в источнике.
        sleep()
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newSourceFileData)

        doSync()
    }


    // ==== sync ====>
    open fun file_changed_by_size_in_source_and_in_target_by_time() {
        prepareSourceDeepFileAndSyncItWithTarget()

        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, newBigSourceFileData)

        sleep()
        fileHelper.createDeepFileInTarget(sDeepDirName, sFileName, newTargetFileData)

        doSync()
    }


    private fun prepareSourceDeepFileAndSyncItWithTarget() {
        fileHelper.createDeepFileInSource(sDeepDirName, sFileName, sFileData)

        doSync()

        assertSourceDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInSource(sDeepDirName, sFileName, sFileData)

        assertTargetDirChildCount(1)
        assertOnlyDeepFileExistsAndContainsInTarget(sDeepDirName, sFileName, sFileData)
    }

    private fun sleep() {
        TimeUnit.SECONDS.sleep(1)
    }
}