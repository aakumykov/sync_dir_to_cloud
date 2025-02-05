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

    override fun whenDeletedAndNew() = ProcessingSteps.BACKUP_AND_DELETE
    override fun whenDeletedAndDeleted() = ProcessingSteps.DO_NOTHING
    override fun whenDeletedAndUnchanged() = ProcessingSteps.BACKUP_AND_DELETE
    override fun whenDeletedAndModified() = ProcessingSteps.BACKUP_AND_DELETE

    override fun whenUnchangedAndNew() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenUnchangedAndDeleted() = ProcessingSteps.COPY
    override fun whenUnchangedAndUnchanged() = ProcessingSteps.DO_NOTHING
    override fun whenUnchangedAndModified() = ProcessingSteps.BACKUP_AND_COPY

    override fun whenModifiedAndNew() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenModifiedAndDeleted() = ProcessingSteps.COPY
    override fun whenModifiedAndUnchanged() = ProcessingSteps.BACKUP_AND_COPY
    override fun whenModifiedAndModified() = ProcessingSteps.BACKUP_AND_COPY

    override fun newAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenNewAndNew()
            StateInStorage.UNCHANGED -> whenNewAndUnchanged()
            StateInStorage.MODIFIED -> whenNewAndModified()
            else -> whenNewAndDeleted()
        }
    }

    override fun deletedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenDeletedAndNew()
            StateInStorage.UNCHANGED -> whenDeletedAndUnchanged()
            StateInStorage.MODIFIED -> whenDeletedAndModified()
            else -> whenDeletedAndDeleted()
        }
    }

    override fun unchangedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenUnchangedAndNew()
            StateInStorage.UNCHANGED -> whenUnchangedAndUnchanged()
            StateInStorage.MODIFIED -> whenUnchangedAndModified()
            else -> whenUnchangedAndDeleted()
        }
    }

    override fun modifiedAnd(targetState: StateInStorage?): ProcessingSteps {
        return when(targetState) {
            StateInStorage.NEW -> whenModifiedAndNew()
            StateInStorage.UNCHANGED -> whenModifiedAndUnchanged()
            StateInStorage.MODIFIED -> whenModifiedAndModified()
            else -> whenModifiedAndDeleted()
        }
    }
}