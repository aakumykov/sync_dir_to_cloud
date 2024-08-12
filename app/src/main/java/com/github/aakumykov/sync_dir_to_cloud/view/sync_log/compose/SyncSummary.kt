package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyncSummary(
    taskId: String,
    executionId: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "taskId: $taskId, executionId: $executionId",
        fontSize = 16.sp,
        modifier = modifier.fillMaxWidth().padding(16.dp)
    )
}