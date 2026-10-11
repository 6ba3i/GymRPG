package com.gymrpg.core.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.annotation.TargetApi

object NotificationCapability {
    internal fun status(api: Int, notificationPermission: Boolean, promoted: Boolean?): String = when {
        api < 36 -> "Live Updates require Android API 36+; standard workout notification fallback"
        !notificationPermission -> "Notification permission unavailable; request only when starting a workout"
        promoted == true -> "Promotion allowed by Android; eligible workout notification and OEM display unverified"
        promoted == false -> "Promotion unavailable; standard workout notification fallback"
        else -> "Promotion status unknown; standard workout notification fallback"
    }

    @TargetApi(36)
    fun read(context: Context): String {
        val api = Build.VERSION.SDK_INT
        val manager = context.getSystemService(NotificationManager::class.java)
        val permission = api < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        // The API checks permission/eligibility; it does not demonstrate that an OEM island displayed a notification.
        val promoted = if (api >= 36) manager?.canPostPromotedNotifications() else null
        return status(api, permission, promoted)
    }
}
