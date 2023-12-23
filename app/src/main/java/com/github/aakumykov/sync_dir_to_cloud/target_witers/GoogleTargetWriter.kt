package com.github.aakumykov.sync_dir_to_cloud.target_witers

import com.github.aakumykov.kotlin_playground.target_writers.TargetWriter
import com.github.aakumykov.kotlin_playground.target_writers.TargetWriterAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class GoogleTargetWriter @AssistedInject constructor(@Assisted authToken: String) : TargetWriter {

    override fun writeToTarget() {
        
    }

    @AssistedFactory
    interface Factory : TargetWriterAssistedFactory {
        override fun create(authToken: String): GoogleTargetWriter
    }

    companion object {
        val TAG: String = GoogleTargetWriter::class.java.simpleName
    }
}