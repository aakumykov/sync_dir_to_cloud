package com.github.aakumykov.sync_dir_to_cloud.file_instructions_processor

interface FileOperationLogMessageSupplier {
    val operationMessageIdStarted: Int
    val operationMessageIdFinished: Int
    val operationMessageIdCancelled: Int
    val operationMessageIdError: Int

    val operationDescriptionStarted: String
    val operationDescriptionFinished: String
    val operationDescriptionCancel: String?
    val operationDescriptionError: String?
}