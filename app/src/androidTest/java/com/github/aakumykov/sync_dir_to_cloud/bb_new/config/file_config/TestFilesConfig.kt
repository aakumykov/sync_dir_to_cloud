package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config

interface TestFilesConfig {

    val FILE_1_NAME: String
    val FILE_2_NAME: String

    val FILE_1_ORIG_SIZE: Int
    val FILE_2_ORIG_SIZE: Int

    val FILE_1_MOD_SIZE: Int
    val FILE_2_MOD_SIZE: Int

    val DIR_1_NAME: String
    val DIR_2_NAME: String

    val TWO_LEVEL_DIR_NAME: String
}