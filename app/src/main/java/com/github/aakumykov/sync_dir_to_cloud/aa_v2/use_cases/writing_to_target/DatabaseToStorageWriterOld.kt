package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
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
class DatabaseToStorageWriterOld @Inject constructor(
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

        // TODO: перевести на Flow
        val allObjectsForTask = syncObjectReader.getAllObjectsForTask(syncTask.id)

        // новые папки
        createNewDirs(allObjectsForTask)

        // исчезнувшие папки
        createInTargetMissingDirs(allObjectsForTask)

        // новые файлы
        syncNewFiles(allObjectsForTask)

        // изменившиеся файлы
        syncModifiedFiles(allObjectsForTask)

        // исчезнувшие файлы
        syncInTargetMissingDirs(allObjectsForTask)


        /*syncObjectReader.getAllObjectsForTask(syncTask.id)
            .filter { !it.isDir }
            .forEach { syncObject ->

                val objectId = syncObject.id

                try {
                    val sourceFileAbsolutePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
                    val targetFileAbsolutePath = syncObject.absolutePathIn(syncTask.targetPath!!)

                    cloudReader
                        ?.getFileInputStream(sourceFileAbsolutePath)
                        // FIXME: асинхронность (Result) + асинхронность (корутины) это перебор.
                        ?.getOrThrow()
                        ?.also { inputStream ->
                            syncObjectStateChanger.markAsBusy(objectId)

                            cloudWriter?.putFile(
                                inputStream,
                                targetFileAbsolutePath,
                                overwriteIfExists)

                            syncObjectStateChanger.markAsSuccessfullySynced(objectId)
                        }

                } catch (t: Throwable) {
                    ExceptionUtils.getErrorMessage(t)?.also { errorMsg ->
                        syncObjectStateChanger.markAsError(objectId, errorMsg)
                        Log.e(TAG, errorMsg, t)
                    }
                }
        }*/
    }


    private suspend fun createNewDirs(allObjectsForTask: List<SyncObject>) {
        allObjectsForTask
            .filter { it.isDir }
            .filter { it.modificationState == ModificationState.NEW }
            .forEach { syncObject ->
                Log.d(TAG, "Новая папка: ${syncObject.relativeParentDirPath}/${syncObject.name}")
            }
    }


    private fun createInTargetMissingDirs(allObjectsForTask: List<SyncObject>) {
        allObjectsForTask
            .filter { it.isDir }
            .filter { !it.isExistsInTarget }
            .forEach { syncObject ->
                Log.d(TAG, "Исчезнувшая папка: ${syncObject.relativeParentDirPath}/${syncObject.name}")
            }
    }


    private fun syncNewFiles(allObjectsForTask: List<SyncObject>) {
        allObjectsForTask
            .filter { !it.isDir }
            .filter { it.modificationState == ModificationState.NEW }
            .forEach { syncObject ->
                Log.d(TAG, "Новый файл: ${syncObject.relativeParentDirPath}/${syncObject.name}")
            }
    }


    private fun syncModifiedFiles(allObjectsForTask: List<SyncObject>) {
        allObjectsForTask
            .filter { !it.isDir }
            .filter { it.modificationState == ModificationState.MODIFIED }
            .forEach { syncObject ->
                Log.d(TAG, "Изменённый файл: ${syncObject.relativeParentDirPath}/${syncObject.name}")
            }
    }


    private fun syncInTargetMissingDirs(allObjectsForTask: List<SyncObject>) {
        allObjectsForTask
            .filter { it.isDir }
            .filter { !it.isExistsInTarget }
            .forEach { syncObject ->
                Log.d(TAG, "Исчезнувшая папка: ${syncObject.relativeParentDirPath}/${syncObject.name}")
            }
    }


    companion object {
        val TAG: String = DatabaseToStorageWriterOld::class.java.simpleName
    }
}