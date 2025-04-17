package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config

interface FileConfig {

    val DEFAULT_FILE_SIZE: Int

    val FILE_1_NAME: String
    val FILE_2_NAME: String

    val FILE_1_SIZE: Int
    val FILE_2_SIZE: Int

    val FILE_1_SIZE_MOD: Int
    val FILE_2_SIZE_MOD: Int
}