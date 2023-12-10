package com.github.aakumykov.notificator

import com.github.aakumykov.interfaces.NotificationInfo
import com.github.aakumykov.interfaces.SyncTaskNotificator
import javax.inject.Inject

class Notificator @Inject constructor(

) : SyncTaskNotificator {

    init {
        
    }

    override fun showNotification(notificationInfo: NotificationInfo): String {
        TODO("Not yet implemented")
    }

    override fun updateNotification(notificationId: String, notificationInfo: NotificationInfo) {
        TODO("Not yet implemented")
    }

    override fun hideNotification(notificationId: String) {
        TODO("Not yet implemented")
    }
}