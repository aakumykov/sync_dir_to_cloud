package com.github.aakumykov.sync_dir_to_cloud.repository.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem

@Dao
interface InstructionLoggingDAO {

    @Insert
    suspend fun add(instructionLogItem: InstructionLogItem)

    @Update
    suspend fun update(instructionLogItem: InstructionLogItem) }