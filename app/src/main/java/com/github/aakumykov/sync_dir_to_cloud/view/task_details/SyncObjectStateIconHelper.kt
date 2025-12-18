package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import androidx.annotation.DrawableRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState

@Deprecated("Не используется")
class SyncObjectStateIconHelper {
    companion object {
        @DrawableRes
        fun getIconFor(syncState: ExecutionState): Int {
            return when(syncState) {
                ExecutionState.NEVER -> R.drawable.ic_task_state_scheduled
                ExecutionState.RUNNING -> R.drawable.ic_task_state_running
                ExecutionState.SUCCESS -> R.drawable.ic_task_state_success
                ExecutionState.ERROR -> R.drawable.ic_task_state_error
                ExecutionState.CANCELLED -> R.drawable.ic_task_state_cancelled
            }
        }
    }
}
