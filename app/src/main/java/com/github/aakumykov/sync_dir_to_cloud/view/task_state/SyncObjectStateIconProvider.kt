package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import androidx.annotation.DrawableRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState

class SyncObjectStateIconProvider {
    companion object {
        @DrawableRes
        fun getIconFor(syncState: ExecutionState): Int {
            return when(syncState) {
                ExecutionState.NEVER -> R.drawable.ic_task_state_scheduled
                ExecutionState.RUNNING -> R.drawable.ic_task_state_running
                ExecutionState.SUCCESS -> R.drawable.ic_task_state_success
                ExecutionState.ERROR -> R.drawable.ic_task_state_error
            }
        }
    }

}
