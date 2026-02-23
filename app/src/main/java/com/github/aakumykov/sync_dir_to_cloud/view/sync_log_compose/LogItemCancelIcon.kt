package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

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