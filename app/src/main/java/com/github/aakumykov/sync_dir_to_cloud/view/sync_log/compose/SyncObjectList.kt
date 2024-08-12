package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

@Composable
fun SyncObjectList(
    list: SnapshotStateList<SyncObjectLogItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(list.size) { index ->
            Text(text = list[index].name, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
        }
    }
}