package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierFactory
import com.github.aakumykov.yandex_disk_cloud_reader.YandexDiskCloudReader
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import java.io.InputStream
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: внедрять SyncTaskReader, CloudAuthReader, а то и YnadexCloudReader
class YandexSourceFileStreamSupplier @AssistedInject constructor(
    @Assisted private val taskId: String,
) : SourceFileStreamSupplier {

    private var yandexCloudReader: YandexDiskCloudReader? = null


    override suspend fun getSourceFileStream(absolutePath: String): Result<InputStream> {
        return suspendCoroutine { continuation ->
            thread {
                getSourceFileStreamSimple(absolutePath).let {
                    continuation.resume(it)
                }
            }
        }
    }


    override fun getSourceFileStreamSimple(absolutePath: String): Result<InputStream> {

        App.getAppComponent().also { appComponent ->

            appComponent.getSyncTaskReader().getSyncTaskSimple(taskId).also { syncTask ->

                syncTask.sourceAuthId?.also { authId ->

                    appComponent.getCloudAuthReader().getCloudAuthSimple(authId)?.also { cloudAuth ->

                        yandexCloudReader = YandexDiskCloudReader(
                            cloudAuth.authToken,
                            OkHttpClient(),
                            Gson()
                        )

                    }
                }
            }
        }

        return yandexCloudReader?.getFileInputStreamSimple(absolutePath)
            ?: Result.failure(Exception("YandexCloudReader is null."))
    }


    @AssistedFactory
    interface Factory: SourceFileStreamSupplierFactory {
        override fun create(taskId: String): YandexSourceFileStreamSupplier
    }
}
