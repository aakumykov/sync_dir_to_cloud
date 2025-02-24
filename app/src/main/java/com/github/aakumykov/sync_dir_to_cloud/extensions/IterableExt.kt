package com.github.aakumykov.sync_dir_to_cloud.extensions

import kotlinx.coroutines.processNextEventInCurrentThread

/**
 * Возвращает элементы списка, присутствующие во втором ([other]) списке,
 * что определяется через функцию [areItemsTheSame].
 */
fun <T> Iterable<T>.intersectBy(other: Iterable<T>, areItemsTheSame: (T, T) -> Boolean): Iterable<T> {
    return this.filter { thisItem ->
        other.firstOrNull { otherItem ->
            areItemsTheSame(thisItem, otherItem)
        }?.let { true } ?: false
    }
}

fun <T> Iterable<T>.subtractBy(other: Iterable<T>, areItemsTheSame: (T, T) -> Boolean): Iterable<T> {
    return this.filter { thisItem ->
        other.firstOrNull { otherItem ->
            areItemsTheSame(otherItem, thisItem)
        }?.let { false } ?: true
    }
}