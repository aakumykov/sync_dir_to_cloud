package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.probes

import org.junit.After
import org.junit.Before

open class TBase {

    @Before
    fun prepare() {
        println("Подготовка")
    }

    @After
    fun finish() {
        println("Завершение")
    }
}