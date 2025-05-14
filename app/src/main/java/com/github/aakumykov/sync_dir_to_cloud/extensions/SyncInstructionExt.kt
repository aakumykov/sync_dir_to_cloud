package com.github.aakumykov.sync_dir_to_cloud.extensions

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction


val SyncInstruction.isFile: Boolean get() = !isDir


val Iterable<SyncInstruction>.hasSourceBackups: Boolean get() {
    return firstOrNull { it.isBackupInSource }.let { true }
}


val Iterable<SyncInstruction>.hasTargetBackups: Boolean get() {
    return firstOrNull { it.isBackupInTarget }.let { true }
}