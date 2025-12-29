package com.github.aakumykov.sync_dir_to_cloud.enums

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ComparisonState

enum class StateInStorage {
    UNCHANGED,
    NEW,
    MODIFIED,
    DELETED;

    companion object {
        fun combine(comparisonState: ComparisonState): String {
            return "${comparisonState.sourceObjectState}_${comparisonState.targetObjectState}"
        }

        val UNCHANGED_null: String = "${UNCHANGED}_null"
        val UNCHANGED_UNCHANGED: String = "${UNCHANGED}_${UNCHANGED}"
        val UNCHANGED_NEW: String = "${UNCHANGED}_${NEW}"
        val UNCHANGED_MODIFIED: String = "${UNCHANGED}_${MODIFIED}"
        val UNCHANGED_DELETED: String = "${UNCHANGED}_${DELETED}"
    }
}