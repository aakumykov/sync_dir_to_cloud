package com.github.aakumykov.kotlin_playground.target_writers


import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalTargetWriter @AssistedInject constructor(@Assisted authToken: String) : TargetWriter {

    override fun writeToTarget() {
        
    }

    @AssistedFactory
    interface Factory : TargetWriterAssistedFactory {
        override fun create(authToken: String): LocalTargetWriter
    }

    companion object {
        val TAG: String = LocalTargetWriter::class.java.simpleName
    }
}