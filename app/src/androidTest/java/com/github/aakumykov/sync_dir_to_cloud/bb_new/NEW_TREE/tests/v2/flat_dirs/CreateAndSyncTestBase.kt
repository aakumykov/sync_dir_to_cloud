package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.flat_dirs

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.NoBackupSyncTestBase
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_is_empty.isEmpty
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.random_name.randomName
import org.junit.Assert

abstract class CreateAndSyncTestBase : NoBackupSyncTestBase() {

    protected val dirInSourceName = randomName
    private val dirInTargetName = randomName

    protected val dirInSource get() = fileHelper.dirInSource(dirInSourceName)
    protected val dirInTarget get() = fileHelper.dirInTarget(dirInTargetName)

    protected val sDirInTarget get() = fileHelper.dirInTarget(dirInSourceName)

    /**
     * Создаёт каталог [dirInSourceName] в источнике и синхронизирует его с приёмником.
     * Создавая такое состояние источника и приёмника:
     * [dirInSourceName] ---> [dirInSourceName]
     */
    protected fun prepare() {
        fileHelper.createDirInSource(dirInSourceName)
        doSync()

        Assert.assertTrue(dirInSource.exists())
        Assert.assertTrue(sDirInTarget.exists())

        Assert.assertTrue(dirInSource.isEmpty)
        Assert.assertTrue(sDirInTarget.isEmpty)

        Assert.assertEquals(1, fileHelper.listSourceDir().size)
        Assert.assertEquals(1, fileHelper.listTargetDir().size)
    }
}