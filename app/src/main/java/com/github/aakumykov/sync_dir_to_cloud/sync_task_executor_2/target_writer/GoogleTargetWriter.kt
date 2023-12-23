package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer

import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriter
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor_2.target_writer.interfaces.TargetWriterAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class GoogleTargetWriter @AssistedInject constructor(@Assisted authToken: String) : TargetWriter {

    override fun write() {
        
    }

    @AssistedFactory
    interface Factory : TargetWriterAssistedFactory {
        override fun create(authToken: String): GoogleTargetWriter
    }

    companion object {
        val TAG: String = GoogleTargetWriter::class.java.simpleName
    }
}