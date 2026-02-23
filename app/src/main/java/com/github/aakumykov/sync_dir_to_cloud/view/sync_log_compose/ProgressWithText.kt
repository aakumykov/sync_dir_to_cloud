package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.extensions.toPercentOf100

@Composable
fun ProgressWithText(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.Companion.fillMaxWidth().weight(1f, true)
        )
        Text(
            text = "${progress.toPercentOf100()}%",
            fontSize = 12.sp,
            modifier = Modifier.Companion.padding(start = 8.dp, end = 4.dp)
        )
    }
}