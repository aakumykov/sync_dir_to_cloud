package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.static_files

import android.os.Environment
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalSourceDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.default_dirs.defaultLocalTargetDir
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import org.junit.Assert
import org.junit.Test
import java.io.File

// TODO: тесты, где бы эти методы выдавали ошибку

open class StaticFlatFilesWithoutBackup : StaticFlatFilesBase() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = LocalToLocalSyncNoBackupTaskConfig()


    @Test
    override fun no_files_in_source_and_no_files_in_target() {
        super.no_files_in_source_and_no_files_in_target()
        assertSourceDirChildCount(0)
        assertTargetDirChildCount(0)
    }

    // ==== sync ===>
    @Test
    override fun empty_file_in_source_and_no_files_in_target() {
        super.empty_file_in_source_and_no_files_in_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sFile)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(sFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun no_files_in_source_and_empty_file_in_target() {
        super.no_files_in_source_and_empty_file_in_target()

        Assert.assertEquals(0, fileHelper.countFilesInSource())

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        Assert.assertTrue(tFile.exists())
        Assert.assertTrue(tFile.isEmpty)
    }


    // ==== sync ===>
    @Test
    override fun same_name_empty_files_in_src_and_tgt() {
        super.same_name_empty_files_in_src_and_tgt()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(commonFileInSource)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(commonFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun diff_names_empty_files_in_source_and_target() {
        super.diff_names_empty_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndEmpty(sFile)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndEmpty(tFile)
        assertExistsAndEmpty(sFileInTarget)
    }


    // ==== sync ===>
    @Test
    override fun data_file_in_source_and_no_files_in_target() {
        super.data_file_in_source_and_no_files_in_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndContains(sFileInTarget, sFileData)

        Assert.assertEquals(
            fileHelper.getFileContents(sFile).joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString(),
        )
    }


    // ==== sync ===>
    @Test
    override fun no_files_in_source_and_data_file_in_target() {
        super.no_files_in_source_and_data_file_in_target()

        Assert.assertEquals(0, fileHelper.countFilesInSource())

        Assert.assertEquals(1, fileHelper.countFilesInTarget())
        assertExistsAndContains(tFile, tFileData)
    }


    // ==== sync ===>
    @Test
    override fun same_name_data_files_in_source_and_target() {
        super.same_name_data_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        Assert.assertEquals(1, fileHelper.countFilesInTarget())

        assertExistsAndContains(commonFileInSource, commonFileData)
        assertExistsAndContains(commonFileInTarget, commonFileData)

        Assert.assertEquals(
            fileHelper.getFileContents(commonFileInSource).joinToString(),
            fileHelper.getFileContents(commonFileInTarget).joinToString(),
        )
    }


    // ==== sync ===>
    @Test
    override fun diff_names_data_files_in_source_and_target() {
        super.diff_names_data_files_in_source_and_target()

        Assert.assertEquals(1, fileHelper.countFilesInSource())
        assertExistsAndContains(sFile, sFileData)

        Assert.assertEquals(2, fileHelper.countFilesInTarget())
        assertExistsAndContains(tFile, tFileData)
        assertExistsAndContains(sFileInTarget, sFileData)

        Assert.assertEquals(
            fileHelper.getFileContents(sFile).joinToString(),
            fileHelper.getFileContents(sFileInTarget).joinToString()
        )
    }


    // ===== sync =====>
    @Test
    fun big_file_in_source_and_no_files_in_target_copy_time_probe() {

        val TAG = "big_file_in_source_and_no_files_in_target_copy_time_probe"

        val bigFileName = "debian.iso"
        val bigFileDir = File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS)
        val bigFile = File(bigFileDir, bigFileName)

        Assert.assertTrue(bigFile.exists())
        Assert.assertTrue(bigFile.length() > 1024 * 1024 * 100)

        val sourceFile = File(defaultLocalSourceDir, bigFileName)
        val targetFile = File(defaultLocalTargetDir, bigFileName)

        bigFile.copyTo(sourceFile, true)

        Assert.assertTrue(sourceFile.exists())

        val startTime = currentTime
        doSync()
        val duration = currentTime - startTime

        Assert.assertTrue(targetFile.exists())
        Assert.assertEquals(sourceFile.length(), targetFile.length())

        Log.d(TAG, "время синхронизации одного большого файла '$bigFileName': $duration")
    }
}