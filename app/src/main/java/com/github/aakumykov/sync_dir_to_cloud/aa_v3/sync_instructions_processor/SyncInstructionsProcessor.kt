package com.github.aakumykov.sync_dir_to_cloud.aa_v3.sync_instructions_processor

import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper.DirsBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper.FilesBackuperCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files.SyncTaskFilesCopier
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs.SyncTaskDirsCreator
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncInstructionRepository
import javax.inject.Inject

class SyncInstructionsProcessor @Inject constructor(
    private val syncInstructionRepository: SyncInstructionRepository,

    private val filesBackuperCreator: FilesBackuperCreator,
    private val disBackuperCreator: DirsBackuperCreator,

    private val taskFilesDeleCreator: DirsBackuperCreator,
    private val taskDirsDeleterCreator: FilesBackuperCreator,

    private val syncTaskDirsCreator: SyncTaskDirsCreator,
    private val syncTaskFilesCopier: SyncTaskFilesCopier,
) {
    fun processSyncInstructions() {

    }
}