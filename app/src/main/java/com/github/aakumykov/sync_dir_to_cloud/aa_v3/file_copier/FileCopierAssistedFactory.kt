package com.github.aakumykov.sync_dir_to_cloud.aa_v3.file_copier

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff.SyncStuff
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.CoroutineScope

@AssistedFactory
interface FileCopierAssistedFactory {
    fun create(
        syncStuff: SyncStuff,
        coroutineScope: CoroutineScope,
        executionId: String,
    ): FileCopier
}