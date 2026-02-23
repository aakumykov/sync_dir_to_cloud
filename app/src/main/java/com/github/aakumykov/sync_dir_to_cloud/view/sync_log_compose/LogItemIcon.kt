package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun LogItemIcon(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(
            when (logOfSync.logItemType) {
                LogItemType.BUSY -> R.drawable.ic_sync_log_busy
                LogItemType.SUCCESS -> R.drawable.ic_sync_log_success
                LogItemType.ERROR -> R.drawable.ic_sync_log_error
                LogItemType.CANCELLED -> R.drawable.ic_sync_log_cancelled
            }
        ),
        contentDescription = null,
        modifier = modifier.padding(end = 10.dp),
        tint = when (logOfSync.logItemType) {
            LogItemType.CANCELLED -> Color(0xFFFFA726)
            LogItemType.ERROR -> Color(0xFFEF5350)
            else -> Color(0xFFB9B9B9)
        }
    )
}