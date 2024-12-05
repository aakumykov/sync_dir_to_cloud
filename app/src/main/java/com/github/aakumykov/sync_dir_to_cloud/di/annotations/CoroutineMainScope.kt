package com.github.aakumykov.sync_dir_to_cloud.di.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineMainScope()

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineFileCopyingScope()