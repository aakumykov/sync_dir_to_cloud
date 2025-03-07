package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_04_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.DirCreator5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.DirCreator5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.FileWriter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file.FileWriter5AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_03_intermediate.InputStreamGetter5
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_03_intermediate.InputStreamGetterAssistedFactory5
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

// TODO: передавать готовый DirCreator и FileWriter?...
// TODO: место для ошибок: помимо того, что методы источник/приёмник
//  требуют ещё и соответствующего аргумента.
//  Хотя... нет. Эти фунции приватные, внешние методы лишены этой проблемы.

class SyncObjectCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val fileWriter5AssistedFactory: FileWriter5AssistedFactory,
){
    @Throws(Exception::class)
    suspend fun copyFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInTarget(syncObject)
        else copyFromSourceToTargetReal(syncObject, overwriteIfExists)
    }


    @Throws(Exception::class)
    suspend fun copyFromTargetToSource(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInSource(syncObject)
        else copyFromTargetToSourceReal(syncObject, overwriteIfExists)
    }


    @Throws(Exception::class)
    private suspend fun createDirInTarget(syncObject: SyncObject) {
        if (!syncObject.isDir)
            throw IllegalArgumentException("SyncObject is not a dir: $syncObject")

        dirCreator.createDirInTarget(
            basePath = syncObject.basePathIn(syncTask.targetPath!!),
            dirName = syncObject.name
        )
    }


    @Throws(Exception::class)
    private suspend fun createDirInSource(syncObject: SyncObject) {
        if (!syncObject.isDir)
            throw IllegalArgumentException("SyncObject is not a dir: $syncObject")

        dirCreator.createDirInSource(
            basePath = syncObject.basePathIn(syncTask.targetPath!!),
            dirName = syncObject.name
        )
    }


    @Throws(Exception::class)
    private suspend fun copyFromSourceToTargetReal(
        syncObject: SyncObject,
        overwriteIfExists: Boolean
    ) {
        if (!syncObject.isFile)
            throw IllegalArgumentException("SyncObject it not a file: $syncObject")

        fileWriter.putFileToTarget(
            inputStream = inputStreamGetter.getInputStreamFor(syncObject),
            filePath = syncObject.absolutePathIn(syncTask.targetPath!!),
            overwriteIfExists = overwriteIfExists,
        )
    }


    @Throws(Exception::class)
    private suspend fun copyFromTargetToSourceReal(
        syncObject: SyncObject,
        overwriteIfExists: Boolean
    ) {
        if (!syncObject.isFile)
            throw IllegalArgumentException("SyncObject it not a file: $syncObject")

        fileWriter.putFileToSource(
            inputStream = inputStreamGetter.getInputStreamFor(syncObject),
            filePath = syncObject.absolutePathIn(syncTask.sourcePath!!),
            overwriteIfExists = overwriteIfExists,
        )
    }


    private val inputStreamGetter: InputStreamGetter5
        get() = inputStreamGetterAssistedFactory.create(syncTask)

    private val dirCreator: DirCreator5
        get() = dirCreator5AssistedFactory.create(syncTask)

    private val fileWriter: FileWriter5
        get() = fileWriter5AssistedFactory.create(syncTask)
}


@AssistedFactory
interface SyncObjectCopierAssistedFactory5 {
    fun create(syncTask: SyncTask): SyncObjectCopier5
}