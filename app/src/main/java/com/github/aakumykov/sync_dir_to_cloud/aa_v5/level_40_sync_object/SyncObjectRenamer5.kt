package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_40_sync_object

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathInWithNewName
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.formattedDateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//
// TODO: Хорошо бы создать такую иерархию:
//  ItemRenamer:
//    FileRenamer
//    SyncObjectRenamer
//
class SyncObjectRenamer5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
    private val syncObjectUpdater: SyncObjectUpdater,
) {
    suspend fun renameObjectInSource(syncObject: SyncObject, newName: String) {
        sourceCloudWriter.renameFileOrEmptyDir(
            fromAbsolutePath = syncObject.absolutePathIn(syncTask.sourcePath!!),
            toAbsolutePath = syncObject.absolutePathInWithNewName(syncTask.sourcePath!!, newName),
            overwriteIfExists = false
        )
        syncObjectUpdater.updateName(syncObject.id, newName)
    }

    suspend fun renameObjectInTarget(syncObject: SyncObject, newName: String) {
        targetCloudWriter.renameFileOrEmptyDir(
            fromAbsolutePath = syncObject.absolutePathIn(syncTask.targetPath!!),
            toAbsolutePath = syncObject.absolutePathInWithNewName(syncTask.targetPath!!, newName),
            overwriteIfExists = false
        )
        syncObjectUpdater.updateName(syncObject.id, newName)
    }

    @Deprecated("Использовать SyncObjectCollisionResolver")
    suspend fun renameCollisionInSource(syncObject: SyncObject): String {
        val oldPath = syncObject.absolutePathIn(syncTask.sourcePath!!)

        val newName = newNameFor("source", syncObject)
        val newPath = syncObject.absolutePathInWithNewName(syncTask.sourcePath!!, newName)

        sourceCloudWriter.renameFileOrEmptyDir(oldPath, newPath)
        syncObjectUpdater.updateName(syncObject.id, newName)

        return newName
    }

    @Deprecated("Использовать SyncObjectCollisionResolver")
    suspend fun renameCollisionInTarget(syncObject: SyncObject): String {

        val oldPath = syncObject.absolutePathIn(syncTask.targetPath!!)

        val newName = newNameFor("target", syncObject)
        val newPath = syncObject.absolutePathInWithNewName(syncTask.targetPath!!, newName)

        targetCloudWriter.renameFileOrEmptyDir(oldPath, newPath)
        syncObjectUpdater.updateName(syncObject.id, newName)

        return newName
    }


    private fun newNameFor(prefix: String, syncObject: SyncObject): String {
        return "${syncObject.name}_${prefix}_${formattedDateTime(currentTime())}"
    }


    private val sourceCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getSourceCloudWriter(syncTask)
    }

    private val targetCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getTargetCloudWriter(syncTask)
    }
}


@AssistedFactory
interface SyncObjectRenamerAssistedFactory5 {
    fun create(syncTask: SyncTask): SyncObjectRenamer5
}