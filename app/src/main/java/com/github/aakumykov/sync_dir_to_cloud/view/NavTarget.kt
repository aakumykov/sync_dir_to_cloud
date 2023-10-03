package com.github.aakumykov.sync_dir_to_cloud.view

sealed class NavTarget {
    object Start : NavTarget()
    object Back : NavTarget()
    object List : NavTarget()
    object Add : NavTarget()
    data class Edit(val id: String?) : NavTarget()
}
