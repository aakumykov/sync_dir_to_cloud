package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

@Composable
fun LogItemSubText(logOfSync: LogOfSync, modifier: Modifier = Modifier) {
    if (null != logOfSync.subText) {
        Text(
            text = logOfSync.subText,
            fontSize = 13.sp,
            modifier = modifier
                .padding(top = 5.dp)
                .background(Color(0xFFFCFCFC), shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp)
        )
    }
}