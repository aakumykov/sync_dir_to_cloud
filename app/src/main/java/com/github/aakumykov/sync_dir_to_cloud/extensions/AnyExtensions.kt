package com.github.aakumykov.sync_dir_to_cloud.extensions

import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageAndVideo.hashCode
import com.github.aakumykov.sync_dir_to_cloud.NotificationService

fun tagWithHashCode(): String = NotificationService.TAG + "(${hashCode()})"