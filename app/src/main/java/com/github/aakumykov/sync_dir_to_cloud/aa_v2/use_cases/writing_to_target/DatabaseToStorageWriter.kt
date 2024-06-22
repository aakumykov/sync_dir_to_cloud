package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

/**
 * CloudWriter - записывает из потока в "облако".
 * Нужна приблуда, с помощью которой можно этот поток получить.
 * Из БД я считываю запись о файле, подлежащем синхронизации, знаю путь к нему.
 * У меня есть CloudReader, который может выдавать InputStream для файла.
 * То есть, нужно у специфического CloudReader-а (тип которого записан в SyncTask)
 * получить поток чтения файла из БД.
 *
 * cloudReader = CloudReaderGetter.getCloudReaderFor(syncTask.sourceStorageType)
 * inputStream = cloudReader.getInputStreamFor(syncObject)
 * cloudWriter = CloudWriterGetter.getCloudWriterFor(syncTask.targetStorageType)
 * cloudWriter.putFileFromStream(inputStream)
 *
 */
class DatabaseToStorageWriter @Inject constructor(
    private val syncObjectReader: SyncObjectReader,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudReaderCreator: CloudReaderCreator,
    private val cloudWriterCreator: CloudWriterCreator,
    private val syncObjectStateChanger: SyncObjectStateChanger,
) {
    suspend fun writeFromDatabaseToStorage(syncTask: SyncTask) {

        // FIXME: обыграть
        val overwriteIfExists = true

        val sourceAuth = cloudAuthReader.getCloudAuth(syncTask.sourceAuthId)
        val targetAuth = cloudAuthReader.getCloudAuth(syncTask.targetAuthId)

        val cloudReader = cloudReaderCreator.createCloudReader(syncTask.sourceStorageType, sourceAuth?.authToken)
        val cloudWriter = cloudWriterCreator.createCloudWriter(syncTask.targetStorageType, targetAuth?.authToken)

        syncObjectReader.getAllObjectsForTask(syncTask.id).forEach { syncObject ->

            syncTask.sourcePath?.also { sourcePath ->

                syncObject.absolutePathIn(sourcePath).also { sourceFileAbsolutePath ->

                    cloudReader?.getFileInputStream(sourceFileAbsolutePath)
                        ?.onSuccess { inputStream ->
                            syncObjectStateChanger.markAsBusy(syncObject.id)
                            cloudWriter?.putFile(inputStream, syncObject.relativeParentDirPath, overwriteIfExists)
                            syncObjectStateChanger.markAsSuccessfullySynced(syncTask.id)
                        }
                        ?.onFailure { throwable ->
                            ExceptionUtils.getErrorMessage(throwable)?.also { errorMsg ->
                                syncObjectStateChanger.markAsError(syncTask.id, errorMsg)
                                Log.e(TAG, errorMsg, throwable)
                            }
                        }
                }

            }
        }
    }

    companion object {
        val TAG: String = DatabaseToStorageWriter::class.java.simpleName
    }
}