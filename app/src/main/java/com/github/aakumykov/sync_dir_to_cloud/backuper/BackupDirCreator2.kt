package com.github.aakumykov.sync_dir_to_cloud.backuper

import androidx.core.util.Supplier
import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_reader.absolutePathFrom
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Named

class BackupDirCreator2 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    @Assisted("prefix") private val dirNamePrefixSupplier: Supplier<String>,
    @Assisted("suffix") private val dirNameSuffixSupplier: Supplier<String>,
    @Assisted private val maxCreationAttemptsCount: Int,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    private suspend fun createBaseBackupDirInSource(): String {
        return createDirUntilNotUnique(
            syncSide = SyncSide.SOURCE,
            parentDirPath = syncTask.sourcePath!!,
        )
    }

    private suspend fun createBaseBackupDirInTarget(): String {
        return createDirUntilNotUnique(
            syncSide = SyncSide.TARGET,
            parentDirPath = syncTask.targetPath!!,
        )
    }

    private suspend fun createBackupDirIn(syncSide: SyncSide, parentDirPath: String): String {
        return createDirUntilNotUnique(syncSide = syncSide, parentDirPath = parentDirPath)
    }

    @Throws(RuntimeException::class)
    private suspend fun createDirUntilNotUnique(
        syncSide: SyncSide,
        parentDirPath: String,
    ): String {
        var dirName = newDirName
        var attemptCount = 1
        while(dirExists(syncSide, dirName = dirName, parentDirPath = parentDirPath)) {
           dirName =  newDirName
            if (attemptCount++ > maxCreationAttemptsCount)
                throw RuntimeException("Unable to create dir '$dirName' in path '$parentDirPath' for $attemptCount times.")
        }
        return createDir(syncSide, dirName, parentDirPath)
    }

    private val newDirName: String get() = dirNamePrefixSupplier.get() + dirNameSuffixSupplier.get()

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
        return absolutePathFrom(parentDirPath,dirName)
    }


    private val sourceCloudWriter: CloudWriter get() = cloudWriterGetter.getSourceCloudWriter(syncTask)
    private val targetCloudWriter: CloudWriter get() = cloudWriterGetter.getTargetCloudWriter(syncTask)

    private val sourceCloudReader: CloudReader get() = cloudReaderGetter.getSourceCloudReaderFor(syncTask)
    private val targetCloudReader: CloudReader get() = cloudReaderGetter.getSourceCloudReaderFor(syncTask)
}


@AssistedFactory
interface BackupDirCreator2AssistedFactory {
    fun create(
        syncTask: SyncTask,
        @Assisted("prefix") dirNamePrefixSupplier: Supplier<String>,
        @Assisted("suffix") dirNameSuffixSupplier: Supplier<String>,
        maxCreationAttemptsCount: Int,
    ): BackupDirCreator2
}