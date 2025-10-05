package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests

import com.github.aakumykov.sync_dir_to_cloud.bb_new.scenario.sync.RunSyncScenario
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import org.junit.Assert
import org.junit.Test

class SyncTest : SyncTestBase() {

    //
    // Не выдумываю фантастических сценариев, воспроизвожу реалистичный
    //
    /**
    0) Нет файлов ни там, ни там [sync_two_empty_dirs]

    1) Файл появляется в источнике [new_file_in_source]
    1.1) Меняется в источнике [modified_file_in_source_unchanged_in_target]
    1.2) Меняется в приёмнике [modified_file_in_target_and_unchanged_in_source]
    1.3) Меняется и там, и там [modified_file_in_source_and_modified_in_target]
    1.4) Удаляется в источнике [file_deleted_in_target]
    1.5) Удаляется в приёмнике [file_deleted_in_source]
    1.6) Удаляется и там, и там [file_deleted_in_source_and_target]

    2) Файл появляется в приёмнике [new_file_in_target]
    2.1) Другой файл появляется в источнике [other_file_in_source]
    2.2) Одноимённый файл появляется в источнике [same_file_in_source]

    3) Каталог первого уровня появляется в источнике [new_dir_in_source]
    3.1) удаляется в источнике [dir_deleted_in_source]
    3.2) появляется в приёмнике [new_dir_in_target]
    3.3) появляется одноимённый в источнике. [same_dir_in_source]

    4) Каталог второго уровня появляется в источнике [two_level_dir_in_source]
    4.1) удаляется из источника [two_level_dir_deleted_from_source]
    4.2) удаляется из приёмника [two_level_dir_deleted_from_target]
    4.3) появляется в приёмнике [two_devel_dir_in_target]
    4.4) одноимённый в источнике [same_two_level_dir_in_source]

    5) Файл и рядом каталог с файлом [small_tree_in_source]
    */

    @Test
    fun sync_two_empty_dirs() = run {
        doSync()
        checkSourceDirIsEmpty()
        checkTargetDirIsEmpty()
    }


    /*@Test
    fun new_file_in_source() {
        fileHelper.createSourceFile1()
        sync()
        Assert.assertEquals(
            fileHelper.sourceFile1Content(),
            fileHelper.targetFile1Content()
        )
    }*/

    /*
        // Ошибочный при массовом запуске
        // Ошибочный при индивидуальном запуске
        @Test
        fun modified_file_in_source_unchanged_in_target() = run {

            new_file_in_source()

            Thread.sleep(sleepTimeoutMs)

            fileHelper.modifySourceFile1()

            Assert.assertNotEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content()
            )

            syncAndCheckFile1Equals()
        }


        // Ошибочный при массовом запуске
        // Ошибочный при индивидуальном запуске
        @Test
        fun modified_file_in_target_and_unchanged_in_source() {

            new_file_in_source()

            Thread.sleep(sleepTimeoutMs)

            fileHelper.modifyTargetFile1()
            Assert.assertNotEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content()
            )

            sync()
            syncAndCheckFile1Equals()
        }


        // Запустился при массовом запуске после [sync_two_empty_dirs].
        // Запустился при индивидуальном запуске.

        // Ошибочный при массовом запуске после [sync_two_empty_dirs].
        // Запустился при индивидуальном запуске.
        // Ошибочный при индивидуальном запуске.
        @Test
        fun modified_file_in_source_and_modified_in_target() {

            new_file_in_source()

            Thread.sleep(sleepTimeoutMs)

            // FIXME: а где создание файла в приёмнике?
            fileHelper.modifyTargetFile1()
            fileHelper.modifySourceFile1()

            Assert.assertNotEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content()
            )

            sync()
            syncAndCheckFile1Equals()
        }


        @Test
        fun file_deleted_in_target() {

            new_file_in_source()

            fileHelper.deleteTargetFile1()
            Assert.assertFalse(fileHelper.targetFile1Exists())

            sync()
            syncAndCheckFile1Equals()
        }


        @Test
        fun file_deleted_in_source() {

            new_file_in_source()

            fileHelper.deleteSourceFile1()
            Assert.assertFalse(fileHelper.sourceFile1Exists())

            sync()
            Assert.assertFalse(fileHelper.targetFile1Exists())
        }


        @Test
        fun file_deleted_in_source_and_target() {

            new_file_in_source()

            fileHelper.deleteSourceFile1()
            Assert.assertFalse(fileHelper.sourceFile1Exists())

            fileHelper.deleteTargetFile1()
            Assert.assertFalse(fileHelper.targetFile1Exists())

            sync()
            Assert.assertFalse(fileHelper.sourceFile1Exists())
            Assert.assertFalse(fileHelper.targetFile1Exists())
        }


        @Test
        fun new_file_in_target() {
            fileHelper.createTargetFile1()

            Assert.assertTrue(fileHelper.targetFile1.exists())
            sync()
            Assert.assertFalse(fileHelper.sourceFile1.exists())
        }


        @Test
        fun other_file_in_source() {
            new_file_in_source()

            fileHelper.createSourceFile2()
            Assert.assertTrue(fileHelper.sourceFile2.exists())

            sync()
            Assert.assertTrue(fileHelper.sourceFile1.exists())
            Assert.assertTrue(fileHelper.targetFile2.exists())
        }


        @Test
        fun same_file_in_source() {

            new_file_in_target()

            fileHelper.createSourceFile1()
            sync()

            Assert.assertTrue(fileHelper.sourceFile1.exists())
            Assert.assertTrue(fileHelper.targetFile1.exists())
        }


        @Test
        fun new_dir_in_source() {
            fileHelper.createDir1InSource()
            Assert.assertTrue(fileHelper.sourceDir1.exists())

            sync()
            Assert.assertTrue(fileHelper.targetDir1.exists())
        }

        @Test
        fun dir_deleted_in_source() {
            new_dir_in_source()

            fileHelper.deleteDir1InSource()
            Assert.assertFalse(fileHelper.sourceDir1.exists())

            sync()
            Assert.assertFalse(fileHelper.targetDir1.exists())
        }

        @Test
        fun new_dir_in_target() {
            fileHelper.createDir1InTarget()
            Assert.assertTrue(fileHelper.targetDir1.exists())

            sync()
            Assert.assertFalse(fileHelper.sourceDir1.exists())
        }

        @Test
        fun same_dir_in_source() {
            new_dir_in_target()

            fileHelper.createDir1InSource()

            sync()
            Assert.assertTrue(fileHelper.sourceDir1.exists())
            Assert.assertTrue(fileHelper.targetDir1.exists())
        }

        @Test
        fun two_level_dir_in_source() {

            fileHelper.createDirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME)
            Assert.assertTrue(fileHelper.dirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())

            sync()
            Assert.assertTrue(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
        }

        @Test
        fun two_level_dir_deleted_from_source() {

            two_level_dir_in_source()

            fileHelper.deleteDirFromSource(testFilesConfig.TWO_LEVEL_DIR_NAME)
            Assert.assertFalse(fileHelper.dirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())

            sync()
            Assert.assertFalse(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
        }

        @Test
        fun two_level_dir_deleted_from_target() {

            two_level_dir_in_source()

            fileHelper.deleteDirFromTarget(testFilesConfig.TWO_LEVEL_DIR_NAME)
            Assert.assertFalse(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())

            sync()
            Assert.assertTrue(fileHelper.dirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
            Assert.assertTrue(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
        }

        @Test
        fun two_devel_dir_in_target() {

            fileHelper.createDirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME)
            Assert.assertTrue(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())

            sync()
            Assert.assertFalse(fileHelper.dirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
            Assert.assertTrue(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
        }

        @Test
        fun same_two_level_dir_in_source() {

            two_devel_dir_in_target()
            two_level_dir_in_source()

            Assert.assertTrue(fileHelper.dirInSource(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
            Assert.assertTrue(fileHelper.dirInTarget(testFilesConfig.TWO_LEVEL_DIR_NAME).exists())
        }


        @Test
        fun small_tree_in_source() {
            fileHelper.createDir1InSource()
            Assert.assertTrue(fileHelper.sourceDir1.exists())

            fileHelper.createSourceFile1()
            Assert.assertTrue(fileHelper.sourceFile1.exists())

            val sourceFileInDir = File(fileHelper.sourceDir1, testFilesConfig.FILE_1_NAME)
            fileHelper.createFileOfSize(sourceFileInDir)
            Assert.assertTrue(sourceFileInDir.exists())

            sync()

            Assert.assertTrue(fileHelper.targetFile1.exists())
            Assert.assertTrue(fileHelper.targetDir1.exists())
            val targetFileIndir = File(fileHelper.sourceDir1, testFilesConfig.FILE_1_NAME)
            Assert.assertTrue(targetFileIndir.exists())

            Assert.assertEquals(fileHelper.sourceFile1Content(),fileHelper.targetFile1Content())
            Assert.assertEquals(fileHelper.fileContents(sourceFileInDir),fileHelper.fileContents(targetFileIndir))
        }*/


        private fun checkSourceDirIsEmpty() {
            checkDirIsEmpty(SyncSide.SOURCE)
        }

        private fun checkTargetDirIsEmpty() {
            checkDirIsEmpty(SyncSide.TARGET)
        }

        private fun checkDirIsEmpty(syncSide: SyncSide) {
            Assert.assertEquals(
                0,
                when(syncSide) {
                    SyncSide.SOURCE -> fileHelper.listSourceDir()
                    SyncSide.TARGET -> fileHelper.listTargetDir()
                }.size
            )
        }


        /*private fun syncAndCheckFile1Equals() {
            sync()
            Assert.assertEquals(
                fileHelper.sourceFile1Content(),
                fileHelper.targetFile1Content()
            )
        }*/
}