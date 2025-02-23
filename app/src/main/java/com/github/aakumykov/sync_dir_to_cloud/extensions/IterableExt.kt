package com.github.aakumykov.sync_dir_to_cloud.extensions

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