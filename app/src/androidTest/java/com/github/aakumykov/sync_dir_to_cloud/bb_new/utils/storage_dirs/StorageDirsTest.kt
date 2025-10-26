package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.storage_dirs

import android.os.Environment
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageDirsTest {

    @Test
    fun global_val_downloads_dir_equals_shared_storage_downloads_dir() {
        Assert.assertEquals(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            downloadsDir
        )
    }
}