package com.github.aakumykov.sync_dir_to_cloud.bb_new.utils.target_context

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry

val targetContext: Context
    get() = InstrumentationRegistry.getInstrumentation().targetContext
