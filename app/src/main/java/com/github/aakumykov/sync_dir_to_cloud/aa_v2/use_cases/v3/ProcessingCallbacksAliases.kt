package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

typealias OnSyncObjectProcessingBegin = suspend (syncObject: SyncObject) -> Unit
typealias OnSyncObjectProcessingSuccess = suspend (syncObject: SyncObject) -> Unit
typealias OnSyncObjectProcessingFailed = suspend (syncObject: SyncObject, throwable: Throwable) -> Unit