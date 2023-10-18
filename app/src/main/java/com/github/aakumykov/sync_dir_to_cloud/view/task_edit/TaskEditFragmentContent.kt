package com.github.aakumykov.sync_dir_to_cloud.view.task_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.aakumykov.sync_dir_to_cloud.R

@Composable
fun TaskEditFragmentContent() {

    val (sourcePath, setSourcePath) = remember { mutableStateOf("") }
    val (targetPath, setTargetPath) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = sourcePath,
                placeholder = { Text(stringResource(R.string.hint_source_path_input)) },
                onValueChange = { setSourcePath(it) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_more_horiz),
                    contentDescription = stringResource(id = R.string.description_select_source_path_button),
                    modifier = Modifier.background(color = MaterialTheme.colors.surface)
                )
            }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = targetPath,
                placeholder = { Text(stringResource(R.string.hint_target_path_input)) },
                onValueChange = { setTargetPath(it) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_more_horiz),
                    contentDescription = stringResource(id = R.string.description_select_source_path_button),
                    modifier = Modifier.background(color = MaterialTheme.colors.surface)
                )
            }
        }

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.BUTTON_task_edit_select_auth))
        }

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.BUTTON_task_edit_save))
        }

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.BUTTON_task_edit_cancel))
        }
    }
}

@Composable
@Preview/*(showSystemUi = true, device = Devices.PIXEL)*/
fun TaskEditFragmentContentPreview() {
    TaskEditFragmentContent()
}