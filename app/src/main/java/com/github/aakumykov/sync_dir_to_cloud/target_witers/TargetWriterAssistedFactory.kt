package com.github.aakumykov.kotlin_playground.target_writers

interface TargetWriterAssistedFactory {
    fun create(authToken: String): TargetWriter
}
