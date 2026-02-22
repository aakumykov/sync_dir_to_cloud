package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun LogItemProgress(logOfSync: LogOfSync, modifier: Modifier = Modifier) {

    if (LogItemAbout.FILE == logOfSync.logItemAbout) {
        LinearProgressIndicator(
            progress = { logOfSync.progress ?: 0f },
            modifier = modifier.fillMaxWidth().padding(top = 8.dp),
        )
    }
}

@Composable
@Preview
fun LogItemProgressPreview() {
    LogItemProgress(LogOfSync.createPreviewStub(LogItemAbout.FILE))
}