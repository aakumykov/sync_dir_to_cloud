package com.github.aakumykov.sync_dir_to_cloud.iterable_ext

data class User(val id: String, val name: String) {
    fun sameByName(otherUser: User): Boolean = this.name == otherUser.name
}