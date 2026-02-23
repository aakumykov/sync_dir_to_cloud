package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun LogItemProgress(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    if (LogItemAbout.FILE == logOfSync.logItemAbout) {
        ProgressWithText(
            logOfSync.progress ?: 0f,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun LogItemProgressPreview() {
    LogItemProgress(LogOfSync.createPreviewStub(LogItemAbout.FILE))
}
