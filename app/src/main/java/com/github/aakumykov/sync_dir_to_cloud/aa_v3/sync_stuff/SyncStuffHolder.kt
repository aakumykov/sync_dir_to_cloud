package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_stuff

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@ExecutionScope
class SyncStuffHolder @Inject constructor() {

    private val map: ConcurrentMap<String, SyncStuff> = ConcurrentHashMap()

    fun get(taskId: String): SyncStuff? = map[taskId]

    fun put(taskId: String, syncStuff: SyncStuff) {
        map[taskId] = syncStuff
    }
}