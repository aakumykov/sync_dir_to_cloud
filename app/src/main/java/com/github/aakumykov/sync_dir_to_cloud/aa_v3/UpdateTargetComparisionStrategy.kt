package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage

/*
+======================+=====+=========+===========+==========+
| Стратегия «обновить приёмник»                               |
+======================+=====+=========+===========+==========+
|                      | NEW | DELETED | UNCHANGED | MODIFIED |
+----------------------+-----+---------+-----------+----------+
| NEW                  | Б,К | К       | К         | Б,К      |
+----------------------+-----+---------+-----------+----------+
| DELETED              | Б,У | -       | Б,У       | Б,У      |
+----------------------+-----+---------+-----------+----------+
| UNCHANGED            | Б,К | К       | -         | Б,К      |
+----------------------+-----+---------+-----------+----------+
| MODIFIED             | Б,К | К       | Б,К       | Б,К      |
+----------------------+-----+---------+-----------+----------+
 */
class UpdateTargetComparisionStrategy : ComparisionStrategy() {
    override fun whenNewAndNew() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenNewAndDeleted() = ProcessingSteps.COPY
    override fun whenNewAndUnchanged() = ProcessingSteps.COPY
    override fun whenNewAndModified() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenNewAndNotExists() = ProcessingSteps.COPY

    override fun whenDeletedAndNew() = ProcessingSteps.BACKUP_AND_DELETE
    override fun whenDeletedAndDeleted() = ProcessingSteps.DO_NOTHING
    override fun whenDeletedAndUnchanged() = ProcessingSteps.BACKUP_AND_DELETE
    override fun whenDeletedAndModified() = ProcessingSteps.BACKUP_AND_DELETE
    override fun whenDeletedAndNotExists() = ProcessingSteps.DO_NOTHING

    override fun whenUnchangedAndNew() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenUnchangedAndDeleted() = ProcessingSteps.COPY
    override fun whenUnchangedAndUnchanged() = ProcessingSteps.DO_NOTHING
    override fun whenUnchangedAndModified() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenUnchangedAndNotExists() = ProcessingSteps.COPY

    override fun whenModifiedAndNew() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenModifiedAndDeleted() = ProcessingSteps.COPY
    override fun whenModifiedAndUnchanged() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenModifiedAndModified() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenModifiedAndNotExists() = ProcessingSteps.COPY

    override fun whenNotExistsAndNew() = ProcessingSteps.DO_NOTHING
    override fun whenNotExistsAndDeleted() = ProcessingSteps.DO_NOTHING
    override fun whenNotExistsAndUnchanged() = ProcessingSteps.DO_NOTHING
    override fun whenNotExistsAndModified() = ProcessingSteps.DO_NOTHING
    override fun whenNotExistsAndNotExists() = ProcessingSteps.DO_NOTHING


    override fun newAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenNewAndNew()
            StateInStorage.UNCHANGED -> whenNewAndUnchanged()
            StateInStorage.MODIFIED -> whenNewAndModified()
            StateInStorage.DELETED -> whenNewAndDeleted()
            null -> whenNewAndNotExists()
        }
    }

    override fun deletedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenDeletedAndNew()
            StateInStorage.UNCHANGED -> whenDeletedAndUnchanged()
            StateInStorage.MODIFIED -> whenDeletedAndModified()
            StateInStorage.DELETED -> whenDeletedAndDeleted()
            null -> whenDeletedAndNotExists()
        }
    }

    override fun unchangedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenUnchangedAndNew()
            StateInStorage.UNCHANGED -> whenUnchangedAndUnchanged()
            StateInStorage.MODIFIED -> whenUnchangedAndModified()
            StateInStorage.DELETED -> whenUnchangedAndDeleted()
            null -> whenUnchangedAndNotExists()
        }
    }

    override fun modifiedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenModifiedAndNew()
            StateInStorage.UNCHANGED -> whenModifiedAndUnchanged()
            StateInStorage.MODIFIED -> whenModifiedAndModified()
            StateInStorage.DELETED -> whenModifiedAndDeleted()
            null -> whenModifiedAndNotExists()
        }
    }
}