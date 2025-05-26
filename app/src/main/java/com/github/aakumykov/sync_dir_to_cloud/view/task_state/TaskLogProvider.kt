package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.compose
import javax.inject.Inject


typealias TaskLogs = List<TaskLogEntry>


class TaskLogProvider @AssistedInject constructor(
    @Assisted private val taskId: String,
    private val taskLogRepository: TaskLogRepository,
) {
    private val _taskLogSharedFlow: SharedFlow<TaskLogs> = MutableSharedFlow(replay = 1)
    val taskLogsFlow: Flow<TaskLogs> get() = _taskLogSharedFlow

    init {
        _taskLogSharedFlow

    }
}