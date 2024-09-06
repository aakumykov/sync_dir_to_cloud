package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import dagger.assisted.AssistedFactory

@AssistedFactory
interface DirDeleterAssistedFactory {
    fun create(syncStuff: SyncStuff): DirDeleter
}