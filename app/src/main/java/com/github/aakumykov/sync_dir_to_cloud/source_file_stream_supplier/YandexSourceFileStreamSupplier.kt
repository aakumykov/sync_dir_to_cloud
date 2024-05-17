package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier

import com.github.aakumykov.sync_dir_to_cloud.App
import com.github.aakumykov.sync_dir_to_cloud.App.Companion.appComponent
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream

// TODO: внедрять SyncTaskReader, CloudAuthReader, а то и YnadexCloudReader
class YandexSourceFileStreamSupplier @AssistedInject constructor(
    @Assisted private val taskId: String,
) : SourceFileStreamSupplier
{
    override suspend fun getSourceFileStream(absolutePath: String): Result<InputStream> {

        return with(appComponent) {

            getSyncTaskReader().getSyncTask(taskId).let { syncTask ->

                syncTask.sourceAuthId?.let { authId ->

                    getCloudAuthReader().getCloudAuth(authId)?.let { cloudAuth ->

                        App.cloudReadersComponent.getYandexCloudReader(cloudAuth.authToken)
                            .getFileInputStream(absolutePath)

                    } ?: Result.failure(Exception("CloudAuth with id '${authId}' not found."))

                } ?: Result.failure(Exception("SyncTask with id '${taskId}' not found."))

                // Не убирай эту неиспользуемую ветку, нужно добавить null-абельность в SyncTaskReader.getSyncTask()
            } ?: Result.failure(Exception("SyncTask with id '${taskId}' not found."))
        }
    }


    @AssistedFactory
    interface Factory: SourceFileStreamSupplierFactory {
        override fun create(taskId: String): YandexSourceFileStreamSupplier
    }
}
