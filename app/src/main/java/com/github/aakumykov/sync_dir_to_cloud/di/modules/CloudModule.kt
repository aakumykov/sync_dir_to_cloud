package com.github.aakumykov.sync_dir_to_cloud.di.modules

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppContext
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.yandex_disk_file_lister.FileListerYandexDiskClient
import dagger.Module
import dagger.Provides

@Module
class CloudModule {

    @Provides
    @AppScope
    fun provideOkHttp3Client(): okhttp3.OkHttpClient {
        return okhttp3.OkHttpClient.Builder().build()
    }
}
