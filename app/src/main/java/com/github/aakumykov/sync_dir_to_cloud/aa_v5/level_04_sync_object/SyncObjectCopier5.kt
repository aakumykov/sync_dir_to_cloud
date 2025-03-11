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
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncMode
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.basePathIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * SyncObject - это объект БД, но его копирование подразумевает
 * операцию над физическим файлом/каталогом. Эти два элемента - запись в БД
 * и файлы в хранилище - идут параллельно, поэтому и обрабатываются параллельно
 * в одном классе. Хотя, здесь можно долго рассуждать: например, что из SyncObject
 * в БД берётся только информация о файле, так что "SyncObjectCopier" должен
 * заниматься только физическими изменениями, а операции с БД нужно выносить
 * в отдельный класс.
 */
class SyncObjectCopier5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted private val executionId: String,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory5,
    private val fileWriter5AssistedFactory: FileWriter5AssistedFactory,
    private val dirCreator5AssistedFactory: DirCreator5AssistedFactory,
    private val syncObjectRegistratorAssistedFactory: SyncObjectRegistratorAssistedFactory,
){
    @Deprecated("Разобраться, как сочетается overwriteIfExists с бекапом")
    @Throws(Exception::class)
    suspend fun copyFromSourceToTarget(syncObject: SyncObject, overwriteIfExists: Boolean) {
        if (syncObject.isDir) createDirInTarget(syncObject)
        else copyFromSourceToTargetReal(syncObject, overwriteIfExists)
    }


    @Deprecated("Разобраться, как сочетается overwriteIfExists с бекапом")
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
            basePath = syncObject.basePathIn(syncTask.sourcePath!!),
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
            inputStream = inputStreamGetter.getInputStreamInSource(syncObject),
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
            inputStream = inputStreamGetter.getInputStreamInTarget(syncObject),
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

    private val registrator: SyncObjectRegistrator
        get() = syncObjectRegistratorAssistedFactory.create(syncTask, executionId)
}


@AssistedFactory
interface SyncObjectCopierAssistedFactory5 {
    fun create(syncTask: SyncTask, executionId: String): SyncObjectCopier5
}