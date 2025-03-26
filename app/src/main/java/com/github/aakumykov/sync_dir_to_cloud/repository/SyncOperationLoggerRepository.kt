package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncOperationLoggerDAO
import javax.inject.Inject

class SyncOperationLoggerRepository @Inject constructor(
    private val syncOperationLoggerDAO: SyncOperationLoggerDAO
){

}
