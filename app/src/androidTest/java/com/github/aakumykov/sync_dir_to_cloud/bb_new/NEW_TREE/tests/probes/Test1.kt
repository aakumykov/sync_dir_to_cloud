package com.github.aakumykov.sync_dir_to_cloud.bb_new.NEW_TREE.tests.probes

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Test1 : TBase() {

    @Test
    fun test1() {
        repeat(2) {
            println("Тест-1")
        }
    }
}