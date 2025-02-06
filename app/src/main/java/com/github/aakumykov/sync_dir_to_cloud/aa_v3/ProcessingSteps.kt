package com.github.aakumykov.sync_dir_to_cloud.aa_v3

data class ProcessingSteps(
    val firstAction: ProcessingAction,
    val secondAction: ProcessingAction,
) {
    val needToBackup: Boolean get() = ProcessingAction.BACKUP == firstAction

    companion object {
        val BACKUP_AND_COPY = ProcessingSteps(ProcessingAction.BACKUP, ProcessingAction.COPY)
        val BACKUP_AND_DELETE = ProcessingSteps(ProcessingAction.BACKUP, ProcessingAction.DELETE)
        val COPY = ProcessingSteps(ProcessingAction.DO_NOTHING, ProcessingAction.COPY)
        val DO_NOTHING = ProcessingSteps(ProcessingAction.DO_NOTHING, ProcessingAction.DO_NOTHING)
    }
}