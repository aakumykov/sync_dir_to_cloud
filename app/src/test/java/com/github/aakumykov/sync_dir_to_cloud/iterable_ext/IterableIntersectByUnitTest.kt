package com.github.aakumykov.sync_dir_to_cloud.iterable_ext

import com.github.aakumykov.sync_dir_to_cloud.extensions.intersectBy
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.IterableCommonFunctions.Companion.assertContainsAllElements
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.IterableCommonFunctions.Companion.assertSizeEquals
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.UserCommonFunctions.Companion.areUsersEqualsByName
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.UserCommonFunctions.Companion.assertIdsEquals
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.UserCommonFunctions.Companion.assertNamesEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class IterableIntersectByUnitTest {

    /**

      ▓▓▓▒░░░
    ▓▓▓▓▒▒░░░
     ▓▓▓▒░░░

    Объединение непустых, имеющих общие элементы:
    * прямо [when_intersect_1_2_lists_with_common_items_then_result_equals_expected]
        - размер
        - содержимое
    * обратно [when_intersect_2_1_lists_with_common_items_then_result_equals_expected]
        - размер
        - содержимое

    Объединение непустых, но без общих элементов:
    * прямо [when_intersect_1_3_lists_without_common_items_then_result_is_empty]
        - результат пустой
    * обратно [when_intersect_3_1_lists_without_common_items_then_result_is_empty]
        - результат пустой

    Объединение непустых одинаковых:
    [when_intersect_with_itself_then_result_size_equals_original_list]
        - размер
        - содержимое

    Объединение непустого и пустого:
    [when_intersect_non_empty_list_with_empty_then_result_is_empty]
        - пустой результат
    Объединение пустого и непустого:
    [when_intersect_empty_list_with_non_empty_then_result_is_empty]
        - пустой результат

    Объединение двух пустых:
    * прямо
        - пустой результат
    * обратно
        - пустой результат
     */

    private val list1: List<User> = listOf(
        User("u1", "User-1"),
        User("u2", "User-2"),
        User("u3", "User-3"),
        User("u4", "User-4"),
        User("u5", "User-5"),
    )

    private val list2: List<User> = listOf(
        User("u11", "User-1"),
        User("u55", "User-5"),
        User("u88", "User-8"),
    )

    private val list3 = listOf(
        User("u777", "User-7")
    )

    //
    // Результат пересечения 1 с 2 должен состоять из элементов 1-ого списка.
    //
    private val intersection_1_2_result = listOf(
        User("u1", "User-1"),
        User("u5", "User-5"),
    )

    //
    // Результат пересечения 2 с 1 должен состоять из элементов 2-ого списка.
    //
    private val intersection_2_1_result = listOf(
        User("u11", "User-1"),
        User("u55", "User-5"),
    )

    private val emptyList1 = emptyList<User>()

    private val emptyList2 = emptyList<User>()

    //
    // Объединение непустых, имеющих общие элементы (прямое)
    //
    @Test
    fun when_intersect_1_2_lists_with_common_items_then_result_equals_expected() {
        val res = list1.intersectBy(list2, ::areUsersEqualsByName)
        assertSizeEquals(res, intersection_1_2_result)
        assertIdsEquals(res, intersection_1_2_result)
        assertNamesEquals(res, intersection_1_2_result)
        assertContainsAllElements(res, intersection_1_2_result)
    }

    //
    // Объединение непустых, имеющих общие элементы (обратное)
    //
    @Test
    fun when_intersect_2_1_lists_with_common_items_then_result_equals_expected() {
        val res = list2.intersectBy(list1, ::areUsersEqualsByName)
        assertSizeEquals(res, intersection_2_1_result)
        assertIdsEquals(res, intersection_2_1_result)
        assertNamesEquals(res, intersection_2_1_result)
        assertContainsAllElements(res, intersection_2_1_result)

    }

    //
    // Объединение непустых, но без общих элементов (прямое)
    //
    @Test
    fun when_intersect_1_3_lists_without_common_items_then_result_is_empty() {
        assertTrue(
            list1.intersectBy(list3, ::areUsersEqualsByName).toList().isEmpty()
        )
    }

    //
    // Объединение непустых, но без общих элементов (обратное)
    //
    @Test
    fun when_intersect_3_1_lists_without_common_items_then_result_is_empty() {
        assertTrue(
            list3.intersectBy(list1, ::areUsersEqualsByName).toList().isEmpty()
        )
    }


    //
    // Объединение непустых одинаковых
    //
    @Test
    fun when_intersect_with_itself_then_result_size_equals_original_list() {
        val res = intersect(list1, list1)
        assertSizeEquals(res, list1)
        assertIdsEquals(res, list1)
        assertNamesEquals(res, list1)
        assertContainsAllElements(res, list1)
    }


    //
    // Объединение непустых одинаковых
    //
    @Test
    fun when_intersect_with_itself_then_result_content_equals_orig_content() {
        val res = list1.intersectBy(list1) { i1,i2 -> i1.sameByName(i2) }
        assertSizeEquals(res, list1)
        assertIdsEquals(res, list1)
        assertNamesEquals(res, list1)
        assertContainsAllElements(res, list1)
    }


    //
    // Объединение непустого и пустого
    //
    @Test
    fun when_intersect_non_empty_list_with_empty_then_result_is_empty() {
        val res = list1.intersectBy(emptyList()) { i1, i2 -> i1.sameByName(i2) }
        assertTrue(res.toList().isEmpty())
    }


    //
    // Объединение пустого и непустого
    //
    @Test
    fun when_intersect_empty_list_with_non_empty_then_result_is_empty() {
        val res = list1.intersectBy(emptyList()) { i1, i2 -> i1.sameByName(i2) }
        assertTrue(res.toList().isEmpty())
    }

    //
    // Объединение двух пустых (прямо)
    //
    @Test
    fun when_intersect_1_2_two_empty_lists_then_result_is_empty() {
        assertTrue(
            intersect(emptyList1, emptyList2)
                .toList()
                .isEmpty()
        )
    }


    //
    // Объединение двух пустых (обратно)
    //
    @Test
    fun when_intersect_2_1_two_empty_lists_then_result_is_empty() {
        assertTrue(
            intersect(emptyList2, emptyList1)
                .toList()
                .isEmpty()
        )
    }



    private fun intersect(first: Iterable<User>, second: List<User>): Iterable<User> {
        return first.intersectBy(second, ::areUsersEqualsByName)
    }
}