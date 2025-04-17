package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config

import android.os.Build
import android.os.Environment
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.syncTaskWithMode
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import java.io.File

object LocalTaskConfig : TaskConfig {

    init {
        Log.d("LocalTaskConfig","init{}")
    }

    override val TASK_ID = "taskId1"
    override val STORAGE_TYPE = StorageType.LOCAL
    override val SYNC_MODE: SyncMode = SyncMode.SYNC
    override val INTERVAL_HOURS = 0
    override val INTERVAL_MINUTES = 0

    override val SOURCE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    override val TARGET_PATH: String = File(
        Environment.getExternalStorageDirectory(),
        "d${Build.VERSION.SDK_INT}"
    ).absolutePath

    override val SOURCE_DIR: File = File(SOURCE_PATH)
    override val TARGET_DIR: File = File(TARGET_PATH)

    // Геттеры у TASK_SYNC и TASK_MIRROR необходимы: без них
    // создаваемая задача не имеет ряд полей, которые инициализируются ниже.
    override val TASK_SYNC: SyncTask get() = syncTaskWithMode(SyncMode.SYNC, this)
    override val TASK_MIRROR: SyncTask get() = syncTaskWithMode(SyncMode.MIRROR, this)

    override val AUTH_ID = "authId1"
    override val SOURCE_AUTH_ID: String = AUTH_ID
    override val TARGET_AUTH_ID: String = AUTH_ID

    override val AUTH_NAME = "test_auth_local"
    override val TARGET_AUTH_NAME: String = AUTH_NAME
    override val SOURCE_AUTH_NAME = AUTH_NAME

    override val AUTH_TOKEN = "test_auth_token"
    override val SOURCE_AUTH_TOKEN = AUTH_TOKEN
    override val TARGET_AUTH_TOKEN = AUTH_TOKEN

    override val SOURCE_AUTH: CloudAuth = CloudAuth(
        id = SOURCE_AUTH_ID,
        name = SOURCE_AUTH_NAME,
        authToken = SOURCE_AUTH_TOKEN,
        storageType = STORAGE_TYPE
    )
}
