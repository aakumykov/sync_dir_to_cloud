package com.github.aakumykov.sync_dir_to_cloud

object GlobalConstants {
    const val TASK_ID: String = "TASK_ID"
    @Deprecated("добавить префикс KEY_") const val EXECUTION_ID: String = "EXECUTION_ID"

    // TODO: выделить это в "DbFieldNames"
    const val FIELD_TASK_ID = "task_id"
    const val FIELD_EXECUTION_ID = "execution_id"
    const val FIELD_JOB_ID = "job_id"

    const val FIELD_CONTENT_NULL = "null"
}