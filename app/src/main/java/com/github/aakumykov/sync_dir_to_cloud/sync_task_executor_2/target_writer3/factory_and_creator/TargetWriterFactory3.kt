package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer3.TargetWriter3

interface TargetWriterFactory3 {
    fun create(authToken: String, taskId: String): TargetWriter3
}