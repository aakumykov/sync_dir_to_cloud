package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

@Composable
fun SyncLogComposable(modifier: Modifier = Modifier,
                      viewModel: SyncLogViewModel,
                      taskId: String,
                      executionId: String) {

    LaunchedEffect(key1 = Unit) {
        viewModel.startWorking(taskId, executionId)
    }

    val listState = viewModel.logOfSyncListFlow.collectAsState(emptyList())
    val isRunningState = viewModel.isRunningFlow.collectAsState(false)

    AutoScrollingLazyColumn(
        listState = listState,
        isRunningState = isRunningState,
        onItemCancelClicked = { jobId ->
            viewModel.cancelJob(jobId)
        },
        modifier = modifier
    )
}


@Composable
fun AutoScrollingLazyColumn(
    listState: State<List<LogOfSync>>,
    onItemCancelClicked: (jobId: String) -> Unit,
    isRunningState: State<Boolean>,
    modifier: Modifier = Modifier
) {

    val lazyListState = rememberLazyListState()

    LaunchedEffect(listState.value.size) {
        if (listState.value.isNotEmpty()) {
            if (isRunningState.value) {
                lazyListState.scrollToItem(listState.value.size - 1)
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp, bottom = 8.dp)
    ) {
        items(listState.value, key = { it.key }) { logOfSync: LogOfSync ->
            LogListItem(
                logOfSync = logOfSync,
                onCancelClicked = {
                    onItemCancelClicked.invoke(logOfSync.origLogId)
                }
            )
        }
    }
}


@Composable
fun LogListItem(
    logOfSync: LogOfSync,
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .background(Color(0xffeeeeee), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                tint = Color(0xFFB9B9B9)
            )
            Text(
                text = logOfSync.text ?: "",
                fontSize = 16.sp,
            )
            if (logOfSync.isActiveFileOperation)
                CancelIcon(onCancelClicked = onCancelClicked)
        }
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
}


@Composable
fun CancelIcon(
    onCancelClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(R.drawable.ic_task_stop),
        contentDescription = stringResource(R.string.description_cancel_file_operation),
        modifier = modifier
            .clickable(onClick = { onCancelClicked.invoke() })
            .size(64.dp)
    )
}