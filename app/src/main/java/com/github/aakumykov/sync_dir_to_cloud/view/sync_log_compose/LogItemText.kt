package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun LogItemText(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    Text(
        text = logOfSync.text ?: "",
        fontSize = 16.sp,
        modifier = modifier
    )
}