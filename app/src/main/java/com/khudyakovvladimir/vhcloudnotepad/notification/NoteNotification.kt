package com.khudyakovvladimir.vhcloudnotepad.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.vhcloudnotepad.R


class NoteNotification {

    fun createNotification(
        context: Context,
        channelId: String,
        channelName: String,
        title: String?,
        text: String?,
        noteId: Int,
        noteIdForNotification: Int,
    ) {

        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.packageName + "/" + R.raw.notification_sound)
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.setSound(soundUri, audioAttributes)

            notificationManager.createNotificationChannel(notificationChannel)

            val bundle = Bundle()
            bundle.putInt("notificationId", noteIdForNotification)

            val pendingIntent = NavDeepLinkBuilder(context.applicationContext)
                .setGraph(R.navigation.main_graph)
                .setDestination(R.id.newNote)
                .setArguments(bundle)
                .createPendingIntent()

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context.applicationContext, channelId)
                    .setSmallIcon(R.drawable.main)
                    .setLargeIcon((getDrawable(context, R.drawable.main)!! as BitmapDrawable).bitmap)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setSound(Uri.parse(soundUri.toString()))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setLights(-16711936, 0,1)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.main, "open", pendingIntent)

            val finalBuilder = builder.build()
            finalBuilder.flags = Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(1, finalBuilder)
        }
    }
}