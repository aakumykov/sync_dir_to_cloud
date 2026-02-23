package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlin.math.roundToLong

@Composable
fun LogItemProgress(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    if (LogItemAbout.FILE == logOfSync.logItemAbout) {
        ProgressWithText(
            remember { mutableFloatStateOf(logOfSync.progress ?: 0f) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@Preview
fun LogItemProgressPreview() {
    LogItemProgress(LogOfSync.createPreviewStub(LogItemAbout.FILE))
}

/*
fun Float.roundToString(decimalCount: Int): String {
    return "%.${decimalCount}f".format(this)
}*/
