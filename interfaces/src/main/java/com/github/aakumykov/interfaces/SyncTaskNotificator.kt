package com.github.aakumykov.interfaces

interface SyncTaskNotificator {
    fun showNotification(notificationInfo: NotificationInfo): String
    fun updateNotification(notificationId: String, notificationInfo: NotificationInfo)
    fun hideNotification(notificationId: String)
}