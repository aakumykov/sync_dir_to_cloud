package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import com.github.aakumykov.sync_dir_to_cloud.functions.fileNameFromPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

//
// Класс скрывает путь к источнику и приёмнику,
// позволяя работать с ними, как с корневыми каталогами.
//
class SourceTargetFileHelper @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    /**
     * @param relativeDirPath Путь к каталогу относительно каталога
     * источника в задаче. По форме может быть как абсолютным, так и относительным
     * (начинаться со слеша или нет).
     * Например, если путь к источнику в [SyncTask] это "/path/to/source",
     * то вызов метода с аргументом "dir1/dir2" создаст каталог
     * "/path/to/source/dir1/dir2"; с аргументом "/dir3" - "/path/to/source/dir3".
     * @return Полный путь к созданному каталогу.
     */
    fun createDirInSource(relativeDirPath: String): String {
        return sourceCloudWriter.createDir(
            combineFSPaths(syncTask.sourcePath!!, parentPathFor(relativeDirPath)),
            fileNameFromPath(relativeDirPath)
        )
    }

    fun createDirInTarget(relativeDirPath: String): String {
        return targetCloudWriter.createDir(
            combineFSPaths(syncTask.targetPath!!, parentPathFor(relativeDirPath)),
            fileNameFromPath(relativeDirPath)
        )
    }


    fun moveFileInSource(oldAbsoluteFilePath: String, newAbsolutePath: String) {
        sourceCloudWriter.moveFileOrEmptyDir(
            oldAbsoluteFilePath,
            newAbsolutePath,
            true
        )
    }


    fun moveFileInTarget(oldAbsoluteFilePath: String, newAbsolutePath: String) {
        targetCloudWriter.moveFileOrEmptyDir(
            oldAbsoluteFilePath,
            newAbsolutePath,
            true
        )
    }


    private val sourceCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getSourceCloudWriter(syncTask)
    }

    private val targetCloudWriter: CloudWriter by lazy {
        cloudWriterGetter.getTargetCloudWriter(syncTask)
    }
}


@AssistedFactory
interface SourceTargetFileHelperAssistedFactory {
    fun create(syncTask: SyncTask): SourceTargetFileHelper
}