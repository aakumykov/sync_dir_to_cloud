package com.github.aakumykov.kotlin_playground.target_writers


import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexTargetWriter @AssistedInject constructor(@Assisted authToken: String) : TargetWriter {

    override fun writeToTarget() {
        
    }

    @AssistedFactory
    interface Factory : TargetWriterAssistedFactory {
        override fun create(authToken: String): YandexTargetWriter
    }

    companion object {
        val TAG: String = YandexTargetWriter::class.java.simpleName
    }
}