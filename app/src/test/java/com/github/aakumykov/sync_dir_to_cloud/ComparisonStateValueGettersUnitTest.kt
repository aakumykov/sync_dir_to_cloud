package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComparisonStateValueGettersUnitTest {

    @Test
    fun when_target_state_not_deleted_then_notDeletedInTarget_is_true() {
        StateInStorage
            .values()
            .toMutableSet()
            .filterNot { StateInStorage.DELETED == it }
            .forEach { stateInStorage ->
                cs(
                    sourceState = StateInStorage.UNCHANGED,
                    targetState = stateInStorage
                ).also {
                    assertTrue(it.notDeletedInTarget)
                }
            }
    }

    @Test
    fun when_target_state_is_deleted_then_notDeletedInTarget_is_false() {
        StateInStorage
            .values()
            .toMutableSet()
            .forEach { stateInStorage ->
                cs(
                    sourceState = stateInStorage,
                    targetState = StateInStorage.DELETED
                ).also {
                    assertFalse(it.notDeletedInTarget)
                }
            }
    }

    @Test
    fun when_source_state_is_deleted_then_isDeletedInSource_is_true() {
        StateInStorage
            .values()
            .toMutableSet()
            .forEach { stateInStorage ->
                cs(
                    sourceState = StateInStorage.DELETED,
                    targetState = stateInStorage
                )
                    .also {
                    assertTrue(it.isDeletedInSource)
                }
            }
    }

    @Test
    fun when_source_state_not_deleted_then_isDeletedInSource_is_false() {
        StateInStorage
            .values()
            .toMutableSet()
            .filterNot { StateInStorage.DELETED == it }
            .forEach { stateInStorage ->
                cs(
                    sourceState = stateInStorage,
                    targetState = stateInStorage
                )
                    .also {
                        assertFalse(it.isDeletedInSource)
                    }
            }
    }

    

    fun cs(sourceState: StateInStorage, targetState: StateInStorage): ComparisonState = ComparisonState(
        id = randomUUID,
        taskId = randomUUID,
        executionId = randomUUID,
        isDir = false,
        relativePath = "/fake-file.txt",
        sourceObjectId = randomUUID,
        targetObjectId = randomUUID,
        sourceObjectState = sourceState,
        targetObjectState = targetState,
    )
}