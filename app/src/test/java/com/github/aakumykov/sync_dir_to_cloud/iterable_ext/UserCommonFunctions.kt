package com.github.aakumykov.sync_dir_to_cloud.iterable_ext

import junit.framework.TestCase.assertEquals

class UserCommonFunctions {
    companion object {
        fun areUsersEqualsByName(u1: User, u2: User): Boolean = u1.name == u2.name

        fun assertNamesEquals(first: Iterable<User>, second: Iterable<User>) {
            assertEquals(
                first.joinToString("") { it.name },
                second.joinToString("") { it.name }
            )
        }

        fun assertIdsEquals(first: Iterable<User>, second: Iterable<User>) {
            assertEquals(
                first.joinToString("") { it.id },
                second.joinToString("") { it.id }
            )
        }
    }
}