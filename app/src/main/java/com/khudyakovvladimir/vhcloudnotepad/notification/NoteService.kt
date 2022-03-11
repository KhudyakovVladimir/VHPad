package com.khudyakovvladimir.vhcloudnotepad.notification

import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NoteService: NotificationListenerService() {

    private lateinit var noteReceiver: NoteReceiver

    override fun onCreate() {
        super.onCreate()

        noteReceiver = NoteReceiver()
        val intentFilter = IntentFilter()
        //need to set some action
        intentFilter.addAction("")
        registerReceiver(noteReceiver,intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(noteReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}