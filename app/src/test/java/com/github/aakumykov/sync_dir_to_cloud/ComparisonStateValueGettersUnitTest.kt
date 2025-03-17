package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notMutuallyUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComparisonStateValueGettersUnitTest {

    //
    // ComparisonState.notDeletedInTarget
    //
    @Test
    fun when_target_state_not_deleted_then_notDeletedInTarget_is_true() {
        storageStates
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
        storageStates
            .forEach { stateInStorage ->
                cs(
                    sourceState = stateInStorage,
                    targetState = StateInStorage.DELETED
                ).also {
                    assertFalse(it.notDeletedInTarget)
                }
            }
    }


    //
    // ComparisonState.isDeletedInSource
    //
    @Test
    fun when_source_state_is_deleted_then_isDeletedInSource_is_true() {
        storageStates
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
        storageStates
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


    //
    // ComparisonState.notMutuallyUnchanged
    //
    @Test
    fun when_source_or_target_state_not_UNCHANGED_then_notMutuallyUnchanged_is_true() {

        fun notUnchangedStates(): Iterable<StateInStorage> = storageStates.filterNot { StateInStorage.UNCHANGED == it }

        notUnchangedStates().forEach { sourceState ->
            storageStates.forEach { targetState ->
                cs(sourceState, targetState).also {
                    assertTrue(it.notMutuallyUnchanged)
                }
            }
        }

        storageStates.forEach { sourceState ->
            notUnchangedStates().forEach { targetState ->
                cs(sourceState, targetState).also {
                    assertTrue(it.notMutuallyUnchanged)
                }
            }
        }
    }

    @Test
    fun when_source_and_target_are_UNCHANGED_then_notMutuallyUnchanged_is_false() {
        assertFalse(
            cs(StateInStorage.UNCHANGED, StateInStorage.UNCHANGED)
                .notMutuallyUnchanged
        )
    }



    private fun cs(sourceState: StateInStorage, targetState: StateInStorage): ComparisonState = ComparisonState(
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

    private val storageStates: MutableSet<StateInStorage>
        = StateInStorage.values().toMutableSet()
}