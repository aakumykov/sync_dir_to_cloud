package com.github.aakumykov.sync_dir_to_cloud.iterable_ext

import com.github.aakumykov.sync_dir_to_cloud.extensions.subtractBy
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.IterableCommonFunctions.Companion.assertContainsAllElements
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.IterableCommonFunctions.Companion.assertSizeEquals
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.UserCommonFunctions.Companion.assertIdsEquals
import com.github.aakumykov.sync_dir_to_cloud.iterable_ext.UserCommonFunctions.Companion.assertNamesEquals
import org.junit.Test
import junit.framework.TestCase.*

class IterableSubtractByUnitTest {

    /**
      ▓▓▓▒░░░
    ▓▓▓▓▒▒░░░
     ▓▓▓▒░░░

     Два непустых, имеющих общие элементы:
      * прямо
       - размер и содержимое (из элементов первого списка) ожидаемы
       - содержит элементы первого списка
      * обратно
       - размер и содержимое (из элементов второго списка) ожидаемы
       - содержит элементы второго списка

     Два непустых без общих элементов:
      * прямо
       - размер и содержимое соответствует первому списку
      * обратно
       - размер и содержимое соответствует второму списку

     Пустой из непустого:
      - размер и содержимое соответствует первому списку

     Непустой из пустого:
      - результат пустой

     Два пустых:
      - результат пустой
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
        User("u22", "User-2"),
        User("u55", "User-5"),
        User("u66", "User-6"),
    )

    private val list3 = listOf(
        User("u000", "User-0"),
        User("u777", "User-7"),
    )

    private val subtraction_1_2_result = listOf(
        User("u3", "User-3"),
        User("u4", "User-4"),
    )

    private val subtraction_2_1_result = listOf(
        User("u66", "User-6"),
    )

    private val empty_list_1 = emptyList<User>()
    private val empty_list_2 = emptyList<User>()


    //
    // Два непустых, имеющих общие элементы (прямо)
    //
    @Test
    fun when_subtract_1_2_then_result_equals_expected() {
        val res = subtract(list1, list2)
        assertSizeEquals(res, subtraction_1_2_result)
        assertIdsEquals(res, subtraction_1_2_result)
        assertNamesEquals(res, subtraction_1_2_result)
        assertContainsAllElements(res, subtraction_1_2_result)
    }

    //
    // Два непустых, имеющих общие элементы (наоборот)
    //
    @Test
    fun when_subtract_2_1_then_result_equals_expected() {
        val res = subtract(list2, list1)
        assertSizeEquals(res, subtraction_2_1_result)
        assertIdsEquals(res, subtraction_2_1_result)
        assertNamesEquals(res, subtraction_2_1_result)
        assertContainsAllElements(res, subtraction_2_1_result)
    }


    //
    // Два непустых без общих элементов (прямо)
    //
    @Test
    fun when_subtract_1_3_then_result_equals_1(){
        val res = subtract(list1, list3)
        assertSizeEquals(res, list1)
        assertIdsEquals(res, list1)
        assertNamesEquals(res, list1)
        assertContainsAllElements(res, list1)
    }

    //
    // Два непустых без общих элементов (обратно)
    //
    @Test
    fun when_subtract_3_1_then_result_equals_3(){
        val res = subtract(list3, list1)
        assertSizeEquals(res, list3)
        assertIdsEquals(res, list3)
        assertNamesEquals(res, list3)
        assertContainsAllElements(res, list3)
    }

    //
    // Из непустого вычитается пустой
    //
    @Test
    fun when_subtract_empty_from_non_empty_then_result_equals_non_empty() {
        val res = subtract(list1, empty_list_1)
        assertSizeEquals(res, list1)
        assertIdsEquals(res, list1)
        assertNamesEquals(res, list1)
        assertContainsAllElements(res, list1)
    }

    //
    // Из пустого вычитается непустой
    //
    @Test
    fun when_subtract_non_empty_from_empty_then_result_is_empty() {
        val res = subtract(empty_list_1, list1)
        assertTrue(res.toList().isEmpty())
    }

    //
    // Два пустых
    //
    @Test
    fun when_subtract_empty_list_from_empty_list_then_result_is_empty() {
        assertTrue(
            subtract(empty_list_1, empty_list_2).toList().isEmpty()
        )
    }


    private fun subtract(first: Iterable<User>, second: Iterable<User>): Iterable<User> {
        return first.subtractBy(second, UserCommonFunctions::areUsersEqualsByName)
    }
}