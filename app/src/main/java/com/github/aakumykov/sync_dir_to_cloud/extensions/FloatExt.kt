package com.github.aakumykov.sync_dir_to_cloud.extensions

import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(decimalDigitsAfterComma: Int): Float {
    return if (0 == decimalDigitsAfterComma) { Math.round(this) * 1f }
    else (10f.pow(decimalDigitsAfterComma)).let { n: Float -> Math.round(this * n) / n }
}


/*
// Не тестирована.
fun Float.roundTo(decimalCount: Int): Float {
    if (decimalCount < 0)
        throw IllegalArgumentException("Cannot round to negative decimal count ($decimalCount).")
    else {
        val rounder = 10.pow(decimalCount)
        val semiResult: Int = Math.round(this * rounder)
        return if (0 == decimalCount) semiResult.toFloat()
        else 1f * semiResult / rounder
    }
}*/


fun Float.toPercentOf100(): Int {
    return (this.round(2) * 100).roundToInt()
}