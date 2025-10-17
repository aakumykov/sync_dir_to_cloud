package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.dynamic_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase
import org.junit.Assert

abstract class DynamicFlatFilesBase : SyncTestBase() {

    override val inDeviceChangesRelaxationTimeoutMs: Long = 2000

    /**
     * Исходная позиция: файл в источнике, синхронизированный в приёмник:
     * [1] --- [1]
     *
     * Файл в источнике меняет содержимое []
     * Файл в приёмнике меняет содержимое []
     * Файлы в источнике и приёмнике меняют содержимое []
     */


    open fun empty_test() {}


    open fun preparing_test() { prepare() }


    open fun source_file_changed() {
        prepare()
        fileHelper.createFileInSource(sFileName, newSourceFileData)
        doSync(/*true*/)
    }


    open fun target_file_changed() {
        prepare()
        fileHelper.createFileInTarget(tFileName, newTargetFileData)
        doSync(/*true*/)
    }


    private fun prepare() {
        fileHelper.createFileInSource(sFileName, sFileData)

        doSync()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndContains(sFileInTarget, sFileData)

        // Эта проверка в каком-то смысле не нужна, так как
        val sfc = fileHelper.getFileContents(sFile).joinToString()
        val sftc = fileHelper.getFileContents(sFileInTarget).joinToString()

        Assert.assertEquals(
            fileHelper.getFileContents(sFile).joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString()
        )
    }
}