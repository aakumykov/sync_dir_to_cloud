package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.v2._flat_files

import com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.SyncTestBase

abstract class FlatFilesBase : SyncTestBase() {

    /**
     * Пустой файл - нет ничего [empty_file_in_source_and_no_files_in_target]
     * Нет ничего - пустой файл [no_files_in_source_and_empty_file_in_target]
     *
     * Пустой файл - одноимённый пустой файл [same_name_empty_files_in_src_and_tgt]
     * Пустой файл - разноимённый пустой файл [diff_names_empty_files_in_source_and_target]
     *
     *
     * Непустой файл - нет ничего [data_file_in_source_and_no_files_in_target]
     * Нет ничего - непустой файл [no_files_in_source_and_data_file_in_target]
     *
     * Непустой файл - одноимённый непустой файл [same_name_data_files_in_source_and_target]
     * Непустой файл - разноимённый непустой файл [diff_names_data_files_in_source_and_target]
     */


    open fun empty_file_in_source_and_no_files_in_target() {
        fileHelper.createFileInSource(sFileName, emptyData)
        doSync()
    }


    open fun no_files_in_source_and_empty_file_in_target() {
        fileHelper.createFileInTarget(tFileName, emptyData)
        doSync()
    }


    open fun same_name_empty_files_in_src_and_tgt() {
        fileHelper.createFileInSource(commonFileName, emptyData)
        fileHelper.createFileInTarget(commonFileName, emptyData)
        doSync()
    }


    open fun diff_names_empty_files_in_source_and_target() {
        fileHelper.createFileInSource(sFileName, emptyData)
        fileHelper.createFileInTarget(tFileName, emptyData)
        doSync()
    }


    open fun data_file_in_source_and_no_files_in_target() {
        fileHelper.createFileInSource(sFileName, sFileData)
        doSync()
    }


    open fun no_files_in_source_and_data_file_in_target() {
        fileHelper.createFileInTarget(tFileName, tFileData)
        doSync()
    }


    open fun same_name_data_files_in_source_and_target() {
        fileHelper.createFileInSource(commonFileName, commonFileData)
        fileHelper.createFileInTarget(commonFileName, commonFileData)
        doSync()
    }


    open fun diff_names_data_files_in_source_and_target() {
        fileHelper.createFileInSource(sFileName, sFileData)
        fileHelper.createFileInTarget(tFileName, tFileData)
        doSync()
    }
}