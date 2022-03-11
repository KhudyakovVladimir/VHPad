package com.khudyakovvladimir.vhcloudnotepad.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NoteNotifyWork(val context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val data = workerParameters.inputData
        val tag = data.getLong("tag", 0)
        val noteId = data.getInt("noteIdData", 0)
        var noteTitle = data.getString("noteTitleData")
        var noteText = data.getString("noteTextData")
        var noteIdForNotification = data.getInt("noteIdForNotificationData", 0)

        val noteNotification = NoteNotification()
        noteNotification.createNotification(
            context,
            "3",
            "noteChannel",
            noteTitle,
            noteText,
            inputData.getInt("noteIdData", 0),
            inputData.getInt("noteIdForNotificationData", 0)
        )
        return Result.success()
    }
}