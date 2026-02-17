package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.InstructionLoggingDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InstructionLogRepository @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val dao: InstructionLoggingDAO,
){
    suspend fun add(item: InstructionLogItem) = withContext(dispatcher) {
        dao.add(item)
    }

    suspend fun update(item: InstructionLogItem) = withContext(dispatcher) {
        dao.update(
            id = item.id,
            logItemType = item.logItemType,
            subText = item.subText,
            finishTime = item.finishTime
        )
    }
}