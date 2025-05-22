package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.SourceTargetFileHelper
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator.SourceTargetFileHelperAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

//
// Задача класса - создавать специфичные для SyncObject каталоги.
//
class SyncObjectDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val sourceTargetFileHelperAssistedFactory: SourceTargetFileHelperAssistedFactory,
){

    /**
     * Создаёт родительский каталог объекта по указанному пути.
     * @return Полный путь к созданному каталогу.
     */
    fun createParentDirOfObject(syncObject: SyncObject): String = when(syncObject.syncSide) {
        SyncSide.SOURCE -> sourceTargetFileHelper.createDirInSource(syncObject.relativeParentDirPath)
        SyncSide.TARGET -> sourceTargetFileHelper.createDirInTarget(syncObject.relativeParentDirPath)
    }


    @Throws(IllegalArgumentException::class)
    fun createDirFromObject(syncObject: SyncObject): String {

        if (!syncObject.isDir)
            throw IllegalArgumentException("SyncObject is not a dir: $syncObject")

        return when(syncObject.syncSide) {
            SyncSide.SOURCE -> sourceTargetFileHelper.createDirInSource(syncObject.relativeParentDirPath)
            SyncSide.TARGET -> sourceTargetFileHelper.createDirInTarget(syncObject.relativeParentDirPath)
        }
    }

    private val sourceTargetFileHelper: SourceTargetFileHelper by lazy {
        sourceTargetFileHelperAssistedFactory.create(syncTask)
    }
}
