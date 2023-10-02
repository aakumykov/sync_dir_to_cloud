package com.github.aakumykov.sync_dir_to_cloud.view

sealed class NavTarget

object NavStart : NavTarget()
object NavBack : NavTarget()
object NavList : NavTarget()
object NavAdd : NavTarget()
class NavEdit(val id: String?) : NavTarget()
