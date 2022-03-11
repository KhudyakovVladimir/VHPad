package com.khudyakovvladimir.vhcloudnotepad.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NoteReceiver : BroadcastReceiver()  {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(intent)
    }
}