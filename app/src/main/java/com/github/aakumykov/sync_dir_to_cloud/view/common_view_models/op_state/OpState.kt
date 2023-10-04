package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state

import com.github.aakumykov.sync_dir_to_cloud.view.utils.TextMessage

sealed class OpState {
    object Idle: OpState()
    object Success : OpState()
    class Busy(val textMessage: TextMessage): OpState()
    class Error(val errorMessage: TextMessage): OpState()
}
