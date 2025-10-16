package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.static_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.LocalToLocalSyncWithoutBackupTaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import org.junit.Test

class StaticFlatFilesWithBackup : StaticFlatFilesWithoutBackup() {

    /**
     * Запускаю те же тесты, что в варианте без бекапа,
     * но [taskConfig] предоставляю с бекапом.
     */

    override val taskConfig: TaskConfig
        get() = LocalToLocalSyncWithBackupTaskConfig(LocalToLocalSyncWithoutBackupTaskConfig())


    @Test
    override fun empty_file_in_source_and_no_files_in_target() {
        super.empty_file_in_source_and_no_files_in_target()
    }

    @Test
    override fun no_files_in_source_and_empty_file_in_target() {
        super.no_files_in_source_and_empty_file_in_target()
    }

    @Test
    override fun same_name_empty_files_in_src_and_tgt() {
        super.same_name_empty_files_in_src_and_tgt()
    }

    @Test
    override fun diff_names_empty_files_in_source_and_target() {
        super.diff_names_empty_files_in_source_and_target()
    }

    @Test
    override fun data_file_in_source_and_no_files_in_target() {
        super.data_file_in_source_and_no_files_in_target()
    }

    @Test
    override fun no_files_in_source_and_data_file_in_target() {
        super.no_files_in_source_and_data_file_in_target()
    }

    @Test
    override fun same_name_data_files_in_source_and_target() {
        super.same_name_data_files_in_source_and_target()
    }

    @Test
    override fun diff_names_data_files_in_source_and_target() {
        super.diff_names_data_files_in_source_and_target()
    }
}
