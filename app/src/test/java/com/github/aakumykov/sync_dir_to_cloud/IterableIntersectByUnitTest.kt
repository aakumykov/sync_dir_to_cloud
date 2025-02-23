package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import junit.framework.TestCase.*
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import java.util.UUID

class IterableIntersectByUnitTest {

    /*
    1) Два непустых:
        а) разные
            - есть общие элементы:
                * результат непустой
                * длина результата равна ожидаемому
                * содержимое результата равна ожидаемому
            - нет общих элементов
        б) одинаковые (сам с собой)
    2) Непустой с пустым
    3) Два пустых
     */

    data class User(val id: String, val name: String) {
        fun sameByName(otherUser: User): Boolean = this.name == otherUser.name
    }

    val list1: List<User> = listOf(
        User("u1", "User-1"),
        User("u2", "User-2"),
        User("u3", "User-3"),
        User("u4", "User-4"),
        User("u5", "User-5"),
    )

    val list2: List<User> = listOf(
        User("u11", "User-1"),
        User("u55", "User-5"),
        User("u88", "User-8"),
    )

    val list3 = listOf(
        User("u777", "User-7")
    )

    val intersection_1_2_result = listOf(
        User("u1", "User-1"),
        User("u5", "User-5"),
    )

    val intersection_2_1_result = listOf(
        User("u11", "User-1"),
        User("u55", "User-5"),
    )


    @Test
    fun when_intersect_lists_with_common_items_then_result_not_empty() {
        val res = list1.intersectBy(list2) { i1,i2 -> i1.sameByName(i2) }
        assertTrue(res.toList().size > 0)
    }


    @Test
    fun when_intersect_lists_with_common_items_then_result_length_equals_expected() {
        assertEquals(
            list1.intersectBy(list2) { i1,i2 -> i1.sameByName(i2) }.toList().size,
            intersection_1_2_result.size
        )
    }


    @Test
    fun when_intersect_lists_with_common_items_then_result_content_equals_expected() {
        val res = list1.intersectBy(list2) { i1,i2 -> i1.sameByName(i2) }
        with(res.toMutableList()) {
            intersection_1_2_result.forEach { resItem ->
                this.removeIf { it.sameByName(resItem) }
            }
            assertTrue(this.isEmpty())
        }
    }


    @Test
    fun when_intersect_1_2_when_result_contains_elements_from_first_list() {
        val res = list1.intersectBy(list2) { i1,i2 -> i1.sameByName(i2) }
        assertEquals(
            res.toList().map { it.id }.joinToString(""),
            intersection_1_2_result.map { it.id }.joinToString("")
        )
    }

    @Test
    fun when_intersect_2_1_when_result_contains_elements_from_first_list() {
        val res = list2.intersectBy(list1) { i1,i2 -> i1.sameByName(i2) }
        assertEquals(
            res.toList().map { it.id }.joinToString(""),
            intersection_2_1_result.map { it.id }.joinToString("")
        )
    }


    @Test
    fun when_intersect_lists_without_common_items_then_result_is_empty() {
        assertTrue(
            list1.intersectBy(list3) { i1,i2 -> i1.sameByName(i2) }
                .toList().isEmpty()
        )
    }


    @Test
    fun when_intersect_with_itself_then_result_size_equals_orig_size() {
        val res = list1.intersectBy(list1) { i1,i2 -> i1.sameByName(i2) }
        assertEquals(res.toList().size, list1.size)
    }


    @Test
    fun when_intersect_with_itself_then_result_content_equals_orig_content() {
        val res = list1.intersectBy(list1) { i1,i2 -> i1.sameByName(i2) }
        with(list1.toMutableList()) {
            res.forEach { resItem ->
                this.removeIf { it.sameByName(resItem) }
            }
            assertTrue(this.isEmpty())
        }
    }


    @Test
    fun when_intersect_non_empty_list_with_empty_then_result_is_empty() {
        val res = list1.intersectBy(emptyList<User>()) { i1,i2 -> i1.sameByName(i2) }
        assertTrue(res.toList().isEmpty())
    }


    @Test
    fun when_intersect_empty_list_with_non_empty_then_result_is_empty() {
        val res = list1.intersectBy(emptyList<User>()) { i1,i2 -> i1.sameByName(i2) }
        assertTrue(res.toList().isEmpty())
    }


    @Test
    fun when_intersect_two_empty_lists_then_result_is_empty() {
        assertTrue(
            emptyList<User>()
                .intersectBy(emptyList<User>()) { i1,i2 -> i1.sameByName(i2) }
                .toList()
                .isEmpty()
        )
    }
}