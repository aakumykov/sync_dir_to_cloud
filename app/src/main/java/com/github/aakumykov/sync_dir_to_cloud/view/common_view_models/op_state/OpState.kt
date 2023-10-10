package com.github.aakumykov.sync_dir_to_cloud.view.common_view_models.op_state

import com.github.aakumykov.sync_dir_to_cloud.view.view_utils.TextMessage

sealed class OpState {
    object Idle: OpState()
    class Success(val textMessage: TextMessage) : OpState()
    class Busy(val textMessage: TextMessage): OpState()
    class Error(val errorMessage: TextMessage): OpState()
}
