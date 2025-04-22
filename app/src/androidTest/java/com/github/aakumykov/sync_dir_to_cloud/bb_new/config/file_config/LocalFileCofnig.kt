package com.github.aakumykov.sync_dir_to_cloud.bb_new.config.file_config

object LocalFileCofnig : FileConfig {

    override val FILE_1_NAME: String
        get() = "file1.txt"

    override val FILE_2_NAME: String
        get() = "file2.txt"


    override val DEFAULT_FILE_SIZE: Int
        get() = 10


    override val FILE_1_SIZE: Int
        get() = 10

    override val FILE_2_SIZE: Int
        get() = 15


    override val FILE_1_SIZE_MOD: Int
        get() = 11

    override val FILE_2_SIZE_MOD: Int
        get() = 16

    override val DIR_1_NAME: String = "dir1"

    override val DIR_2_NAME: String = "dir2"

    override val TWO_LEVEL_DIR_NAME: String = "dir1/dir1.1"
}
