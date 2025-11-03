package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2.files.flat_files.creation

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncNoBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class CreationFlatFilesWithBackup : CreationFlatFilesWithoutBackup() {

    override val taskConfig: LocalToLocalSyncNoBackupTaskConfig
        get() = localToLocalWithBackupTaskConfig



    @Test
    override fun no_files_in_source_and_target() {
        super.no_files_in_source_and_target()
    }


    @Test
    override fun file_created_in_source() {
        super.file_created_in_source()

    }


    @Test
    override fun file_created_in_target() {
        super.file_created_in_target()
    }


    @Test
    override fun files_created_in_source_and_target() {
        super.files_created_in_source_and_target()
    }
}