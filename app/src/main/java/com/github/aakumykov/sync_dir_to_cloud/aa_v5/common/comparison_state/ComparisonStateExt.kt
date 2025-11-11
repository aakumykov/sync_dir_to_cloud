package com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.comparison_state

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage

val ComparisonState.isFile: Boolean get() = !isDir


val ComparisonState.notDeletedInSource: Boolean
    get() = StateInStorage.DELETED != sourceObjectState

val ComparisonState.isUnchangedInSource: Boolean
    get() = StateInStorage.UNCHANGED == sourceObjectState

val ComparisonState.notDeletedInTarget: Boolean
    get() = targetObjectState != StateInStorage.DELETED

val ComparisonState.isUnchangedInTarget: Boolean
    get() = StateInStorage.UNCHANGED == targetObjectState

val ComparisonState.isDeletedInSource: Boolean
    get() = sourceObjectState == StateInStorage.DELETED

val ComparisonState.isDeletedInTarget: Boolean
    get() = StateInStorage.DELETED == targetObjectState

val ComparisonState.isModifiedOrDeletedInSource: Boolean
    get() = StateInStorage.MODIFIED == sourceObjectState ||
            StateInStorage.DELETED == sourceObjectState

val ComparisonState.notMutuallyUnchanged: Boolean
    get() = !(sourceObjectState == StateInStorage.UNCHANGED &&
            targetObjectState == StateInStorage.UNCHANGED)

val ComparisonState.isUnchangedOrDeletedInSource: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED ||
            sourceObjectState == StateInStorage.DELETED

val ComparisonState.isUnchangedOrDeletedInTarget: Boolean
    get() = targetObjectState == StateInStorage.UNCHANGED ||
            targetObjectState == StateInStorage.DELETED

val ComparisonState.notUnchangedOrDeletedInSource: Boolean
    get() = sourceObjectState != StateInStorage.UNCHANGED &&
            sourceObjectState != StateInStorage.DELETED

val ComparisonState.notUnchangedOrDeletedInTarget: Boolean
    get() = targetObjectState != StateInStorage.UNCHANGED &&
            targetObjectState != StateInStorage.DELETED

val ComparisonState.isNewOrModifiedInSource: Boolean
    get() = StateInStorage.NEW == sourceObjectState ||
            StateInStorage.MODIFIED == sourceObjectState

val ComparisonState.isNewOrModifiedInTarget: Boolean
    get() = StateInStorage.NEW == targetObjectState ||
            StateInStorage.MODIFIED == targetObjectState

val ComparisonState.isNewModifiedDeletedInTarget: Boolean
    get() = StateInStorage.NEW == targetObjectState ||
            StateInStorage.MODIFIED == targetObjectState ||
            StateInStorage.DELETED == targetObjectState

val ComparisonState.isModifiedInSource: Boolean
    get() = StateInStorage.MODIFIED == sourceObjectState

val ComparisonState.isModifiedInTarget: Boolean
    get() = StateInStorage.MODIFIED == targetObjectState


// Дальше однотипные
val ComparisonState.isSourceUnchangedTargetNew: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isSourceUnchangedTargetModified: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isSourceUnchangedTargetDeleted: Boolean
    get() = sourceObjectState == StateInStorage.UNCHANGED
            && targetObjectState == StateInStorage.DELETED

val ComparisonState.isSourceNewAndTargetUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isSourceNewAndTargetNew: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isSourceNewAndTargetModified: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isSourceNewAndTargetDeleted: Boolean
    get() = sourceObjectState == StateInStorage.NEW
            && targetObjectState == StateInStorage.DELETED

val ComparisonState.isSourceModifiedAndTargetUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isSourceModifiedAndTargetNew: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isSourceModifiedAndTargetModified: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isSourceModifiedAndTargetDeleted: Boolean
    get() = sourceObjectState == StateInStorage.MODIFIED
            && targetObjectState == StateInStorage.DELETED

val ComparisonState.isSourceDeletedAndTargetUnchanged: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.UNCHANGED

val ComparisonState.isSourceDeletedAndTargetNew: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.NEW

val ComparisonState.isSourceDeletedAndTargetModified: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.MODIFIED

val ComparisonState.isSourceDeletedAndTargetDeleted: Boolean
    get() = sourceObjectState == StateInStorage.DELETED
            && targetObjectState == StateInStorage.DELETED
