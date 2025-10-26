package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.target_context

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TargetContextTest {

    @Test
    fun target_context_val_equals_device_target_context() {
        Assert.assertEquals(
            InstrumentationRegistry.getInstrumentation().targetContext,
            targetContext
        )
    }
}