package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.github.aakumykov.sync_dir_to_cloud.R

@Composable
fun CloudAuthListMainContent() {
    Column {
        CloudAuthListToolbar(stringResource(R.string.CLOUD_AUTH_LIST_page_title))
    }
}

@Composable
@Preview(device = Devices.PIXEL, showSystemUi = true, showBackground = true)
fun CloudAuthListPreview() {
    MaterialTheme {
        CloudAuthListMainContent()
    }
}