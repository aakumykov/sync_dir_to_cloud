package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config

object LocalFileCofnig : FileConfig {

    override val FILE_1_NAME: String
        get() = "file1.txt"

    override val FILE_1_SIZE: Int
        get() = 10

    override val FILE_1_SIZE_MOD: Int
        get() = 11
}
