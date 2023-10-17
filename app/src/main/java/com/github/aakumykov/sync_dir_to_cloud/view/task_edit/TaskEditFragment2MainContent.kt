package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.style.LightGrey
import com.github.aakumykov.sync_dir_to_cloud.style.Orange
import com.github.aakumykov.sync_dir_to_cloud.style.WhiteSmoke

@Composable
fun TaskEditFragment2MainContent() {

    val sourcePath = remember { mutableStateOf("") }
    val targetPath = remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(Alignment.Top)
        .padding(8.dp)
    ) {

        // Ввод пути источника
        Row (modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp, max = 70.dp)
            .padding(8.dp)
        ) {
            TextField(
                value = sourcePath.value,
                placeholder = { stringResource(id = R.string.hint_source_path_input) },
                onValueChange = { sourcePath.value = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_more_horiz),
                contentDescription = stringResource(R.string.description_select_source_path_button),
                modifier = Modifier
                    .size(36.dp)
                    .background(color = WhiteSmoke, shape = RoundedCornerShape(4.dp))
                    .align(Alignment.CenterVertically)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = WhiteSmoke,
                    )
            )
        }

        // Ввод пути назначения
        Row (modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp, max = 70.dp)
            .padding(8.dp)
        ) {
            TextField(
                value = targetPath.value,
                placeholder = { stringResource(id = R.string.hint_target_path_input) },
                onValueChange = { targetPath.value = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_more_horiz),
                contentDescription = stringResource(R.string.description_select_source_path_button),
                modifier = Modifier
                    .size(36.dp)
                    .background(color = WhiteSmoke, shape = RoundedCornerShape(4.dp))
                    .align(Alignment.CenterVertically)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(4.dp),
                        ambientColor = WhiteSmoke,
                    )
            )
        }


        // Кнопка выбора облачной авторизации
        Row (modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp, max = 70.dp)
            .padding(8.dp)
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(backgroundColor = Orange),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.BUTTON_select_cloud_auth),
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }


        // Кнопка сохранения
        Row (modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp, max = 70.dp)
            .padding(8.dp)
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxSize()
            ) {
                Text(stringResource(id = R.string.BUTTON_task_edit_save), fontSize = 20.sp)
            }
        }

        // Кнопка отмены
        Row (modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp, max = 70.dp)
            .padding(8.dp)
        ) {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightGrey
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Text(stringResource(id = R.string.BUTTON_task_edit_cancel), fontSize = 20.sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL)
@Composable
fun MainContentPreview() {
    TaskEditFragment2MainContent()
}
