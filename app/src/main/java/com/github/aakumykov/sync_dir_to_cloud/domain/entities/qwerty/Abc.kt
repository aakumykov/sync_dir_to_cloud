package com.github.aakumykov.sync_dir_to_cloud.domain.entities.qwerty

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.AbcBase
import java.util.*

class Abc : AbcBase {

    val id: String

    constructor(abcBase: AbcBase) : super(abcBase.sourcePath, abcBase.targetPath) {
        id = UUID.randomUUID().toString()
    }

    constructor(id: String, sourcePath: String, targetPath: String) : super(
        sourcePath,
        targetPath
    ) {
        this.id = id
    }
}