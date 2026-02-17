package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun SyncLogItem(
    logOfSync: LogOfSync,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .background(Color(0xffeeeeee), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.Companion.CenterVertically) {
            LogItemIcon(logOfSync)
            LogItemText(logOfSync)
            LogItemCancelIcon(logOfSync, onCancelClicked = onCancelClicked)
        }
        LogItemSubText(logOfSync)
        LogItemProgress(logOfSync)
    }
}

@Preview
@Composable
fun SyncLogItemPreview() {
    SyncLogItem(
        logOfSync = LogOfSync.createPreviewStub(LogItemAbout.FILE),
        onCancelClicked = {}
    )
}