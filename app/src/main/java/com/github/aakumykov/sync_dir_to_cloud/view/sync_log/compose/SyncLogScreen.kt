package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

@Composable
fun SyncLogScreen(
    taskId: String,
    executionId: String,
    logInfoList: SnapshotStateList<SyncObjectLogItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SyncSummary(taskId, executionId)
        SyncObjectList(logInfoList)
    }
}