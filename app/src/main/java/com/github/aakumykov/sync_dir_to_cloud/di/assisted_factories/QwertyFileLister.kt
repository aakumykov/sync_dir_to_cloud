package com.github.aakumykov.sync_dir_to_cloud.di.assisted_factories

import android.util.Log
import com.github.aakumykov.file_lister.FileLister
import com.github.aakumykov.fs_item.FSItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class QwertyFileLister @AssistedInject constructor(
    @Assisted authToken: String
): FileLister {

    override fun listDir(path: String): List<FSItem> {
        Log.d(TAG, "listDir() called with: path = $path")
        return listOf()
    }

    companion object {
        val TAG: String = QwertyFileLister::class.java.simpleName
    }
}