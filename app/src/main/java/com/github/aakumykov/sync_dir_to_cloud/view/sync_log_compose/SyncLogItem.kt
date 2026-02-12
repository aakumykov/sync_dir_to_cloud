package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun SyncLogItem(modifier: Modifier = Modifier,
                viewModel: SyncLogViewModel,
                taskId: String,
                executionId: String) {

    LaunchedEffect(key1 = Unit) {
        viewModel.startWorking(taskId, executionId)
    }

    val listState = viewModel.logOfSyncListFlow
        .collectAsState(emptyList())

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = listState.value, key = { it.key }) { logOfSync: LogOfSync ->
            Column (
                modifier = Modifier
                    .padding(4.dp)
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
    }
}