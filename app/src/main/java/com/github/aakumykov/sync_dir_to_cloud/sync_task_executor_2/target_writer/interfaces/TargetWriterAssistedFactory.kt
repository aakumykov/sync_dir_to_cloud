package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces

interface TargetWriterAssistedFactory {
    fun create(authToken: String, taskId: String): TargetWriter
}
