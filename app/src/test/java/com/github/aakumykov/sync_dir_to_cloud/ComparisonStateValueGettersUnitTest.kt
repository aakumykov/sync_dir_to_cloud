package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.ComparisonState
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetModified
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.isSourceUnchangedTargetNew
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notDeletedInTarget
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notMutuallyUnchanged
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInSource
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.notUnchangedOrDeletedInTarget
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
        statesInStorage
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
        statesInStorage
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
        statesInStorage
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
        statesInStorage
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

        fun notUnchangedStates(): Iterable<StateInStorage> = statesInStorage.filterNot { StateInStorage.UNCHANGED == it }

        notUnchangedStates().forEach { sourceState ->
            statesInStorage.forEach { targetState ->
                cs(sourceState, targetState).also {
                    assertTrue(it.notMutuallyUnchanged)
                }
            }
        }

        statesInStorage.forEach { sourceState ->
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



    //
    // ComparisonState.notUnchangedOrDeletedInSource
    //
    @Test
    fun when_source_not_UNCHANGED_or_DELETED_then_notUnchangedOrDeletedInSource_is_true() {
        statesInStorage.without(
            StateInStorage.UNCHANGED,
            StateInStorage.DELETED
        ).forEach { sourceState ->
            statesInStorage.forEach { targetState ->
                assertTrue(cs(sourceState, targetState).notUnchangedOrDeletedInSource)
            }
        }
    }

    @Test
    fun when_source_is_UNCHANGED_or_DELETED_then_notUnchangedOrDeletedInSource_is_false() {
        listOf(
            StateInStorage.UNCHANGED,
            StateInStorage.DELETED
        ).forEach { sourceState ->
            statesInStorage.forEach { targetState ->
                assertFalse(cs(sourceState,targetState).notUnchangedOrDeletedInSource)
            }
        }
    }


    //
    // ComparisonState.notUnchangedOrDeletedInTarget
    //
    @Test
    fun when_target_not_UNCHANGED_or_DELETED_then_notUnchangedOrDeletedInTarget_is_true() {
        statesInStorage.forEach { sourceState ->
            statesInStorage.without(
                StateInStorage.UNCHANGED,
                StateInStorage.DELETED
            ).forEach { targetState ->
                assertTrue(cs(sourceState, targetState).notUnchangedOrDeletedInTarget)
            }
        }
    }

    @Test
    fun when_target_is_UNCHANGED_or_DELETED_then_notUnchangedOrDeletedInTarget_is_false() {
        statesInStorage.forEach { sourceState ->
            listOf(
                StateInStorage.UNCHANGED,
                StateInStorage.DELETED
            ).forEach { targetState ->
                assertFalse(cs(sourceState,targetState).notUnchangedOrDeletedInTarget)
            }
        }
    }


    /**
     * [ComparisonState.isSourceUnchangedTargetNew]
     */
    @Test
    fun when_source_UNCHANGED_and_target_NEW_then_isSourceUnchangedTargetNew_is_true() {
        assertTrue(
            cs(
                StateInStorage.UNCHANGED,
                StateInStorage.NEW,
            ).isSourceUnchangedTargetNew
        )
    }

    @Test
    fun when_source_not_UNCHANGED_and_target_not_NEW_then_isSourceUnchangedTargetNew_is_false() {
        statesInStorage.without(StateInStorage.UNCHANGED).forEach { sourceState ->
            statesInStorage.without(StateInStorage.NEW).forEach { targetState ->
                assertFalse(cs(sourceState,targetState).isSourceUnchangedTargetNew)
            }
        }
    }


    /**
     * [ComparisonState.isSourceUnchangedTargetModified]
     */
    @Test
    fun when_source_UNCHANGED_and_target_MODIFIED_then_isSourceUnchangedTargetModified_is_true() {
        assertTrue(cs(
            StateInStorage.UNCHANGED,
            StateInStorage.MODIFIED
        ).isSourceUnchangedTargetModified)
    }

    @Test
    fun when_source_not_UNCHANGED_and_target_not_MODIFIED_then_isSourceUnchangedTargetModified_is_false() {
        statesInStorage.without(StateInStorage.UNCHANGED).forEach { sourceState ->
            statesInStorage.without(StateInStorage.MODIFIED).forEach { targetState ->
                assertFalse(cs(
                    sourceState,targetState
                ).isSourceUnchangedTargetModified)
            }
        }
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

    private val statesInStorage: MutableSet<StateInStorage>
        = StateInStorage.values().toMutableSet()

    fun MutableSet<StateInStorage>.without(vararg statesToRemove: StateInStorage): Iterable<StateInStorage> {
        return toMutableSet().apply {
            statesToRemove.forEach { remove(it) }
        }
    }

}