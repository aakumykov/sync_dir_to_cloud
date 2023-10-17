package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun CloudAuthListToolbar(title: String) {
    TopAppBar {
        IconButton(onClick = {  }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Меню")
        }

        Text(text = title,
            fontSize = 22.sp)

        Spacer(Modifier.weight(1f, true))

        /*IconButton(onClick = { }) {
            Icon(Icons.Filled.Info, contentDescription = "Информация о приложении")
        }

        IconButton(onClick = { }) {
            Icon(Icons.Filled.Search, contentDescription = "Поиск")
        }*/
    }
}

@Composable
@Preview
fun CloudAuthListToolbarPreview() {
    CloudAuthListToolbar("Заголовок")
}