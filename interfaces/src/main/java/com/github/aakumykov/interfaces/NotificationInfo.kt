package com.github.aakumykov.interfaces

import androidx.annotation.DrawableRes

interface NotificationInfo {
    val title: String
    val message: String
    val iconRes: DrawableRes
}