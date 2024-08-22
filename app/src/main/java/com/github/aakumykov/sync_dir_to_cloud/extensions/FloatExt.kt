package com.github.aakumykov.sync_dir_to_cloud.extensions

import kotlin.math.pow

fun Float.round(decimalDigitsAfterComma: Int): Float {
    return if (0 == decimalDigitsAfterComma) { Math.round(this) * 1f }
    else (10f.pow(decimalDigitsAfterComma)).let { n: Float -> Math.round(this * n) / n }
}