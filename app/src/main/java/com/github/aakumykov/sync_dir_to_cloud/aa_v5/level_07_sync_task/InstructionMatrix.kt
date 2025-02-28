package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_07_sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncAction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode

class InstructionMatrix {

    companion object {
        val INSTRUCTION_MATRIX = mapOf(
            SyncMode.UPDATE_LOCAL to mapOf(
                StateInStorage.UNCHANGED to SyncAction.DO_NOTHING,
                StateInStorage.NEW to SyncAction.COPY,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
            SyncMode.SYNC_LOCAL to mapOf(
                StateInStorage.UNCHANGED to SyncAction,
                StateInStorage.NEW to SyncAction,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
            SyncMode.MIRROR_UPDATE to mapOf(
                StateInStorage.UNCHANGED to SyncAction,
                StateInStorage.NEW to SyncAction,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
            SyncMode.MIRROR_SYNC to mapOf(
                StateInStorage.UNCHANGED to SyncAction,
                StateInStorage.NEW to SyncAction,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
            SyncMode.UPDATE_REMOTE to mapOf(
                StateInStorage.UNCHANGED to SyncAction,
                StateInStorage.NEW to SyncAction,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
            SyncMode.SYNC_REMOTE to mapOf(
                StateInStorage.UNCHANGED to SyncAction,
                StateInStorage.NEW to SyncAction,
                StateInStorage.MODIFIED to SyncAction,
                StateInStorage.DELETED to SyncAction,
            ),
        )

        fun getFor(syncMode: SyncMode, syncObject: SyncObject): SyncAction? {
            return INSTRUCTION_MATRIX[syncMode]?.get(syncObject.stateInStorage)
        }
    }
}