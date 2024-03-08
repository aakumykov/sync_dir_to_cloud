package com.github.aakumykov.sync_dir_to_cloud.view.other.menu_helper

import androidx.lifecycle.LiveData

typealias CustomActions = Array<CustomMenuItem>

interface HasCustomActions {
    val customActions: LiveData<CustomActions>
    val customActionsUpdates: LiveData<CustomActionUpdate>?
}
