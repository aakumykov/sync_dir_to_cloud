package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.sync_dir_to_cloud.config.CollisionResolverConfig
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * Разрешение коллизий имён файлов путём переименования нужно производить
 * за один раз, чтобы файлы имели одинаковый, основанный на дате, суффикс (префикс).
 * С этой целью создаётся отдельный класс для этой задачи.
 */
class SyncObjectCollisionResolver @AssistedInject constructor(
    @Assisted syncTask: SyncTask,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val syncObjectRenamer5assistedFactory: SyncObjectRenamerAssistedFactory5,
) {
    suspend fun resolveCollision(sourceObjectId: String, targetObjectId: String) {

        val sourceObject = syncObjectDBReader.getSyncObject(sourceObjectId)
        val targetObject = syncObjectDBReader.getSyncObject(targetObjectId)

        if (null == sourceObject)
            throw IllegalStateException("Source object with id='$sourceObjectId' not found!")

        if (null == targetObject)
            throw IllegalStateException("Target object with id='$sourceObjectId' not found!")

        // Суффикс с датой и временем должен быть единым для источника и приёмника!
        val dateTimeSuffix = formattedDateTime(currentTime)

        val newNameForSource = newNameFor(CollisionResolverConfig.SOURCE_SUFFIX, dateTimeSuffix, sourceObject)
        val newNameForTarget = newNameFor(CollisionResolverConfig.TARGET_SUFFIX, dateTimeSuffix, targetObject)

        syncObjectRenamer.renameObjectInSource(sourceObject, newNameForSource)
        syncObjectRenamer.renameObjectInTarget(targetObject, newNameForTarget)
    }

    private fun newNameFor(prefix: String, suffix: String, syncObject: SyncObject): String {
        return "${syncObject.name}_${prefix}_${suffix}"
    }

    private val syncObjectRenamer: SyncObjectRenamer5 by lazy {
        syncObjectRenamer5assistedFactory.create(syncTask)
    }
}


@AssistedFactory
interface SyncObjectCollisionResolverAssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectCollisionResolver
}