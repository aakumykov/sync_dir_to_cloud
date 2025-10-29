package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files.modification

import com.github.aakumykov.sync_dir_to_cloud.bb_new.config.task_config.TaskConfig
import com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.localToLocalWithBackupTaskConfig
import org.junit.Test

class ModificationFlatFilesWithBackup(numberOfRun: Int) : ModificationFlatFilesBase(numberOfRun) {

    override val taskConfig: TaskConfig
        get() = localToLocalWithBackupTaskConfig


    @Test
    override fun empty_test_() {
        super.empty_test_()
    }


    @Test
    override fun source_file_was_changed_by_size() {
        super.source_file_was_changed_by_size()


    }


    @Test
    override fun source_file_was_changed_by_time() {
        super.source_file_was_changed_by_time()
    }


    @Test
    override fun target_file_was_changed_by_size() {
        super.target_file_was_changed_by_size()
    }


    @Test
    override fun target_file_was_changed_by_time() {
        super.target_file_was_changed_by_time()
    }
}