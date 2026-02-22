package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileInstruction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide


val FileInstruction.isFile: Boolean get() = !isDir


val Iterable<FileInstruction>.hasSourceBackups: Boolean get() {
    return firstOrNull { it.isBackupInSource }.let { true }
}


val Iterable<FileInstruction>.hasTargetBackups: Boolean get() {
    return firstOrNull { it.isBackupInTarget }.let { true }
}

val FileInstruction.notProcessed: Boolean
    get() = !isProcessed

fun FileInstruction.objectIdInSide(syncSide: SyncSide): String? {
    return when(syncSide) {
        SyncSide.SOURCE -> objectIdInSource
        SyncSide.TARGET -> objectIdInTarget
    }
}
