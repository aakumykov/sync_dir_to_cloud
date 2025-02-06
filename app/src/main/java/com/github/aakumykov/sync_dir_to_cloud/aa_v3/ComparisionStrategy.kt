package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

abstract class ComparisionStrategy {

    abstract fun whenNewAndNew(): ProcessingSteps
    abstract fun whenNewAndDeleted(): ProcessingSteps
    abstract fun whenNewAndUnchanged(): ProcessingSteps
    abstract fun whenNewAndModified(): ProcessingSteps

    abstract fun whenDeletedAndNew(): ProcessingSteps
    abstract fun whenDeletedAndDeleted(): ProcessingSteps
    abstract fun whenDeletedAndUnchanged(): ProcessingSteps
    abstract fun whenDeletedAndModified(): ProcessingSteps

    abstract fun whenUnchangedAndNew(): ProcessingSteps
    abstract fun whenUnchangedAndDeleted(): ProcessingSteps
    abstract fun whenUnchangedAndUnchanged(): ProcessingSteps
    abstract fun whenUnchangedAndModified(): ProcessingSteps

    abstract fun whenModifiedAndNew(): ProcessingSteps
    abstract fun whenModifiedAndDeleted(): ProcessingSteps
    abstract fun whenModifiedAndUnchanged(): ProcessingSteps
    abstract fun whenModifiedAndModified(): ProcessingSteps

    fun compare(
        sourceState: StateInStorage,
        targetState: StateInStorage?
    ): ProcessingSteps {
        return when(sourceState) {
            StateInStorage.NEW -> newAnd(targetState)
            StateInStorage.DELETED -> deletedAnd(targetState)
            StateInStorage.UNCHANGED -> unchangedAnd(targetState)
            StateInStorage.MODIFIED -> modifiedAnd(targetState)
        }
    }

    abstract fun newAnd(targetState: StateInStorage?): ProcessingSteps
    abstract fun deletedAnd(targetState: StateInStorage?): ProcessingSteps
    abstract fun unchangedAnd(targetState: StateInStorage?): ProcessingSteps
    abstract fun modifiedAnd(targetState: StateInStorage?): ProcessingSteps
}