package com.github.aakumykov.sync_dir_to_cloud.view.fragments.operation_state

import com.github.aakumykov.sync_dir_to_cloud.view.TextMessage

sealed class OpState {
    object Idle: OpState()
    class Busy(val textMessage: TextMessage): OpState()
    object Success : OpState()
    class Error(val errorMessage: TextMessage): OpState()
}
