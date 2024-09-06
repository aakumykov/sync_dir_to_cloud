package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject

@ExecutionScope
class CancellationHolder @Inject constructor() {

    private val map: ConcurrentMap<String, Job> = ConcurrentHashMap()

    fun add(id: String, job: Job) {
        map[id] = job
    }

    fun get(id: String): Job? = map[id]

    fun remove(id: String) {
        map.remove(id)
    }
}