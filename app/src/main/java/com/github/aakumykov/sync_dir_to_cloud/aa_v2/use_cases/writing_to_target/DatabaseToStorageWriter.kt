package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target

import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.DeletedDirsDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.DeletedFilesDeleter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.LostDirsCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.LostFilesCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.ModifiedFilesCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.NewDirsCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.writing_to_target.dirs.NewFilesCopier
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import javax.inject.Inject

class DatabaseToStorageWriter @Inject constructor(
    // Каталоги
    private val deletedDirsDeleter: DeletedDirsDeleter,
    private val newDirsCreator: NewDirsCreator,
    private val lostDirsCreator: LostDirsCreator,
    // Файлы
    private val deletedFilesDeleter: DeletedFilesDeleter,
    private val newFilesCopier: NewFilesCopier,
    private val modifiedFilesCopier: ModifiedFilesCopier,
    private val lostFilesCopier: LostFilesCopier,
) {
    /*
        I. Синхронизировать каталоги
            1. Удалить удалённые каталоги (стратегия)
            2. Создать новые каталоги (стратегия)
            3. Восстановить пропавшие каталоги (стратегия)
       II. Синхронизировать файлы
            1. Удалить удалённое (стратегия)
            2. Копировать новое (стратегия)
            3. Копировать изменившееся (стратегия)
            4. Восстановить пропавшее (стратегия)
   */
    fun writeToPath(taskId: String,
//                    syncMode: SyncMode,
                    sourceAuth: CloudAuth,
                    targetAuth: CloudAuth) {

    }
}