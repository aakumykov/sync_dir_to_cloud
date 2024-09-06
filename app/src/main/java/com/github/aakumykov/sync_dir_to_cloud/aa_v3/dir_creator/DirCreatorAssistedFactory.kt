package com.github.aakumykov.sync_dir_to_cloud.aa_v3.dir_creator

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.CoroutineScope

@AssistedFactory
interface DirCreatorAssistedFactory {
    fun create(syncStuff: SyncStuff, coroutineScope: CoroutineScope): DirCreator
}