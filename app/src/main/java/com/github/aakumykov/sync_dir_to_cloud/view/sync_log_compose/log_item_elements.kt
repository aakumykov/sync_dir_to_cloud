package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

@Composable
fun LogItemSubText(logOfSync: LogOfSync) {
    if (null != logOfSync.subText) {
        Text(
            text = logOfSync.subText,
            fontSize = 13.sp,
            modifier = Modifier
                .padding(top = 5.dp)
                .background(Color(0xFFFCFCFC), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp)
        )
    }
}


@Composable
fun LogItemText(logOfSync: LogOfSync) {
    Text(
        text = logOfSync.text ?: "",
        fontSize = 16.sp,
    )
}

@Composable
fun LogItemIcon(logOfSync: LogOfSync) {
    Icon(
        painter = painterResource(
            when(logOfSync.logItemType) {
                LogItemType.BUSY -> R.drawable.ic_sync_log_busy
                LogItemType.SUCCESS -> R.drawable.ic_sync_log_success
                LogItemType.ERROR -> R.drawable.ic_sync_log_error
                LogItemType.CANCELLED -> R.drawable.ic_sync_log_cancelled
            }
        ),
        contentDescription = null,
        modifier = Modifier.padding(end = 10.dp),
        tint = when(logOfSync.logItemType) {
            LogItemType.CANCELLED -> Color(0xFFFFA726)
            LogItemType.ERROR -> Color(0xFFEF5350)
            else -> Color(0xFFB9B9B9)
        }
    )
}


@Composable
fun LogItemCancelIcon(
    logOfSync: LogOfSync,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (logOfSync.isActiveFileOperation) {
        Icon(
            painter = painterResource(R.drawable.ic_task_stop),
            contentDescription = stringResource(R.string.description_cancel_file_operation),
            modifier = modifier
                .clickable(onClick = { onCancelClicked.invoke() })
                .size(64.dp)
        )
    }
}


@Composable
fun LogItemProgress(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    if (LogItemAbout.FILE ==logOfSync.logItemAbout) {
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = modifier.fillMaxWidth().padding(top = 8.dp),
        )
    }
}

@Composable
@Preview
fun LogItemProgressPreview() {
    LogItemProgress(LogOfSync.createPreviewStub(LogItemAbout.FILE))
}