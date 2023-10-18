package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.R

@Composable
fun Toolbar(
    title: String,
    homeIcon: ImageVector = Icons.Filled.ArrowBack,
    homeIconDescription: String = stringResource(R.string.description_home_icon_back),
    onHomeClicked: () -> Unit
) {

    TopAppBar {
        IconButton(onClick = { onHomeClicked.invoke() }) {
            Icon(homeIcon, contentDescription = homeIconDescription)
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
    Toolbar(
        title = "Заголовок",
        onHomeClicked = {}
    )
}