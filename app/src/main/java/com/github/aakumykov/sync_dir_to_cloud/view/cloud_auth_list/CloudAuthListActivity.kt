package com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.compose.CloudAuthListMainContent

class CloudAuthListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CloudAuthListMainContent {
                    finish()
                }
            }
        }
    }
}
