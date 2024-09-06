package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_deleter

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.CoroutineScope

@AssistedFactory
interface DirDeleterAssistedFactory {
    fun create(syncStuff: SyncStuff, coroutineScope: CoroutineScope): DirDeleter
}