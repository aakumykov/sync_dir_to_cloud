package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import androidx.annotation.DrawableRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState

class SyncObjectStateIconGetter {
    companion object {
        @DrawableRes
        fun getIconFor(syncState: SyncState): Int {
            return when(syncState) {
                SyncState.NEVER -> R.drawable.ic_task_state_scheduled
                SyncState.RUNNING -> R.drawable.ic_task_state_running
                SyncState.SUCCESS -> R.drawable.ic_task_state_success
                SyncState.ERROR -> R.drawable.ic_task_state_error
            }
        }
    }

}
