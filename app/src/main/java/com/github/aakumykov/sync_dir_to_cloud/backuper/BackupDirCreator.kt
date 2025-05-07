package com.github.aakumykov.sync_dir_to_cloud.backuper

import androidx.core.util.Supplier
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class BackupDirCreator @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted("prefix") private val dirNamePrefixSupplier: Supplier<String>,
    @Assisted("suffix") private val dirNameSuffixSupplier: Supplier<String>,
    @Assisted private val maxCreationAttemptsCount: Int,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    suspend fun createBaseBackupDirInSource(): String {
        return createBackupDirIn(
            syncSide = SyncSide.SOURCE,
            parentDirPath = syncTask.sourcePath!!,
        )
    }

    suspend fun createBaseBackupDirInTarget(): String {
        return createBackupDirIn(
            syncSide = SyncSide.TARGET,
            parentDirPath = syncTask.targetPath!!,
        )
    }


    suspend fun createBackupDirIn(syncSide: SyncSide, parentDirPath: String): String {
        val dirName = getUniqueDirName(syncSide, parentDirPath)
        return createDir(syncSide, dirName, parentDirPath)
    }


    @Throws(RuntimeException::class)
    private suspend fun getUniqueDirName(syncSide: SyncSide, parentDirPath: String): String {

        var dirName = newDirName
        var attemptCount = 1

        while(dirExists(syncSide, dirName = dirName, parentDirPath = parentDirPath)) {
           dirName =  newDirName
            if (attemptCount++ > maxCreationAttemptsCount)
                throw RuntimeException("Unable to create unique dir name in path '$parentDirPath' for $attemptCount times.")
        }

        return dirName
    }


    private val newDirName: String
        get() = "${dirNamePrefixSupplier.get()}_${dirNameSuffixSupplier.get()}"


    private suspend fun dirExists(syncSide: SyncSide, dirName: String, parentDirPath: String): Boolean {
        return when(syncSide) {
            SyncSide.SOURCE -> sourceCloudReader.dirExists(parentDirPath, dirName).getOrThrow()
            SyncSide.TARGET -> targetCloudReader.dirExists(parentDirPath, dirName).getOrThrow()
        }
    }


    private fun createDir(
        syncSide: SyncSide,
        dirName: String,
        parentDirPath: String,
    ): String {
        when(syncSide) {
            SyncSide.SOURCE -> sourceCloudWriter.createDir(parentDirPath, dirName)
            SyncSide.TARGET -> targetCloudWriter.createDir(parentDirPath, dirName)
        }
        return combineFSPaths(parentDirPath, dirName)
    }


    private val sourceCloudWriter: CloudWriter get() = cloudWriterGetter.getSourceCloudWriter(syncTask)
    private val targetCloudWriter: CloudWriter get() = cloudWriterGetter.getTargetCloudWriter(syncTask)

    private val sourceCloudReader: CloudReader get() = cloudReaderGetter.getSourceCloudReaderFor(syncTask)
    private val targetCloudReader: CloudReader get() = cloudReaderGetter.getSourceCloudReaderFor(syncTask)
}


@AssistedFactory
interface BackupDirCreatorAssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("prefix") dirNamePrefixSupplier: Supplier<String>,
        @Assisted("suffix") dirNameSuffixSupplier: Supplier<String>,
        maxCreationAttemptsCount: Int,
    ): BackupDirCreator
}