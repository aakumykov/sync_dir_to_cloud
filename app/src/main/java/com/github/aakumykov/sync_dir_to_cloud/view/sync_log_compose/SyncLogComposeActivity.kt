package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asFlow
import androidx.room.util.TableInfo
import com.github.aakumykov.sync_dir_to_cloud.DaggerViewModelHelper
import com.github.aakumykov.sync_dir_to_cloud.EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.SyncLogViewModel
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose.ui.theme.Sync_dir_to_cloudTheme

class SyncLogComposeActivity : ComponentActivity() {

    private lateinit var syncLogViewModel: SyncLogViewModel

    private val taskId: String get() = intent.getStringExtra(TASK_ID)
    private val executionId: String get() = intent.getStringExtra(EXECUTION_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        syncLogViewModel = DaggerViewModelHelper.get(this, SyncLogViewModel::class.java)

        setContent {
            Sync_dir_to_cloudTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = syncLogViewModel,
                        taskId = taskId,
                        executionId = executionId,
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier,
             viewModel: SyncLogViewModel,
             taskId: String,
             executionId: String) {

    LaunchedEffect (key1 = Unit) {
        viewModel.startWorking(taskId, executionId)
    }

    val listState = viewModel.logOfSync
        .asFlow()
        .collectAsState(emptyList())

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items = listState.value, key = { it.timestamp }) { logOfSync ->
            Column {
                Text(
                    text = logOfSync.text,
                    fontSize = 16.sp
                )
                Text(
                    text = logOfSync.subText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Sync_dir_to_cloudTheme {
        Greeting("Android")
    }
}*/
