package com.github.aakumykov.sync_dir_to_cloud.a0_backupers.v1

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import dagger.assisted.AssistedFactory

@AssistedFactory
interface DirBackuperAssistedFactory {
    fun create(syncStuff: SyncStuff): DirBackuper
}