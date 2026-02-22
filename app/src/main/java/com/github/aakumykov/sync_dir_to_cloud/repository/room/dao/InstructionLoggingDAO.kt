package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem

@Dao
interface InstructionLoggingDAO {

    @Insert
    suspend fun add(instructionLogItem: InstructionLogItem)

    @Query(
        "UPDATE ${InstructionLogItem.TABLE_NAME} " +
                "SET ${BasicLogItem.FIELD_LOG_ITEM_TYPE} = :logItemType, " +
                "${BasicLogItem.FIELD_SUB_TEXT} = :subText, " +
                "${BasicLogItem.FIELD_FINISH_TIME} = :finishTime " +
                "WHERE ${DbFieldNames.FIELD_ID} = :id"
    )
    suspend fun update(
        id: String,
        logItemType: LogItemType,
        subText: String?,
        finishTime: Long?
    )
}