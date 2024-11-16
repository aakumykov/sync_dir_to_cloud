package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log

import androidx.lifecycle.LiveData
import androidx.work.impl.utils.LiveDataUtils
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem

interface ExecutionLogReader {
    fun getExecutionLog(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>>
}