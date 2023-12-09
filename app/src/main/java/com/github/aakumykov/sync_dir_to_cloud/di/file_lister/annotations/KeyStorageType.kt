package com.github.aakumykov.sync_dir_to_cloud.di.file_lister.annotations

import com.github.aakumykov.interfaces.StorageType
import dagger.MapKey

@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class KeyStorageType (val value: StorageType)