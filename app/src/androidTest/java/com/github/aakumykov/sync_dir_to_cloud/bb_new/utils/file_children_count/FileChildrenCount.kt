package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.file_children_count

import java.io.File
import java.lang.NullPointerException
import kotlin.jvm.Throws

val File.childrenCount: Int
    @Throws(NullPointerException::class)
    get() = list()!!.size