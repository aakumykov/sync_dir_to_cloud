package com.github.aakumykov.sync_dir_to_cloud.di.annotations

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class KeyStorageType(val value: StorageType)
