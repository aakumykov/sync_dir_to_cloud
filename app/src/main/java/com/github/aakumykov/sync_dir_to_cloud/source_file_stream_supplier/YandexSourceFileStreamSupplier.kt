package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import com.github.aakumykov.cloud_reader.YandexCloudReader
import com.github.aakumykov.sync_dir_to_cloud.App
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.io.InputStream

// TODO: внедрять SyncTaskReader, CloudAuthReader
class YandexSourceFileStreamSupplier(
    private val taskId: String
) : SourceFileStreamSupplier {

    private var yandexCloudReader: YandexCloudReader? = null


    override suspend fun getSourceFileStream(absolutePath: String): Result<InputStream> {

        App.getAppComponent().also { appComponent ->
            appComponent.getSyncTaskReader().getSyncTask(taskId).also { syncTask ->
                syncTask.sourceAuthId?.also { authId ->
                    appComponent.getCloudAuthReader().getCloudAuth(authId)?.also { cloudAuth ->

                        yandexCloudReader = YandexCloudReader(
                            cloudAuth.authToken,
                            OkHttpClient(),
                            Gson()
                        )

                    }
                }
            }
        }

        return yandexCloudReader?.getFileInputStream(absolutePath)
            ?: Result.failure(Exception("YandexCloudReader is null."))
    }

}
