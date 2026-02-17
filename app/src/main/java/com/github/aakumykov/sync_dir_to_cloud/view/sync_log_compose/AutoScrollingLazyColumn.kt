package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

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
                lazyListState.animateScrollToItem(listState.value.size - 1)
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
            SyncLogItem(
                logOfSync = logOfSync,
                onCancelClicked = {
                    onItemCancelClicked.invoke(logOfSync.origLogId)
                }
            )
        }
    }
}