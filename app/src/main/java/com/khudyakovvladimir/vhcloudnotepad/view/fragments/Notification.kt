package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import com.example.vhcloudnotepad.R
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.khudyakovvladimir.vhcloudnotepad.notification.NoteNotifyWork
import com.khudyakovvladimir.vhcloudnotepad.utils.SoundPoolHelper
import com.khudyakovvladimir.vhcloudnotepad.utils.TimeHelper
import com.khudyakovvladimir.vhcloudnotepad.view.appComponent
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class Notification : Fragment() {

    @Inject
    lateinit var timeHelper: TimeHelper
    private var noteId: Int = 0
    private var noteTitle: String = ""
    private var noteText: String = ""
    private var noteIdForNotification = 0

    override fun onAttach(context: Context) {
        context.appComponent.injectNotification(this)
        super.onAttach(context)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.notification_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonNotification = view.findViewById<Button>(R.id.buttonNotification)
        val date = view.findViewById<DatePicker>(R.id.date)
        val time = view.findViewById<com.khudyakovvladimir.vhcloudnotepad.notification.TimePickerCustom>(R.id.time)

        val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if(sharedPreferences.contains("noteIdForNotification"))
        {
            noteId = sharedPreferences.getInt("noteIdForNotification", 1)
            noteTitle = sharedPreferences.getString("noteTitleForNotification", "")!!
            noteText = sharedPreferences.getString("noteTextForNotification", "")!!
            noteIdForNotification = sharedPreferences.getInt("notificationId", 0)
        }

        buttonNotification.setOnClickListener {

            val customCalendar = Calendar.getInstance()
            customCalendar.set(
                date.year, date.month, date.dayOfMonth, time.hour, time.minute, 0
            )
            val customTime = customCalendar.timeInMillis
            val currentTime = currentTimeMillis()
            val delay = customTime - currentTime

            if (customTime > currentTime) {
                val data = Data.Builder()
                    .putLong("tag", delay)
                    .putInt("notificationId", 0)
                    .putInt("noteIdData", noteId)
                    .putString("noteTitleData", noteTitle)
                    .putString("noteTextData", noteText)
                    .putInt("noteIdForNotificationData", noteIdForNotification)
                    .build()

                createBackgroundNotification(delay, data, delay)

                val timeForNotification = customCalendar.time

                val soundPoolHelper = SoundPoolHelper(activity!!.application)
                soundPoolHelper.playSoundBell(true)

                makeToast(timeHelper.getCurrentTimeForNotification(timeForNotification))
                findNavController().navigate(R.id.notes)
            }
        }
    }

    private fun createBackgroundNotification(delay: Long, data: Data, tag: Long) {
        Log.d("TAG", "createBackgroundNotification() - tag = $tag")
        val oneTimeWorkRequest =
            OneTimeWorkRequest
                .Builder(NoteNotifyWork::class.java)
                .setInitialDelay(delay, MILLISECONDS)
                .setInputData(data)
                .addTag(tag.toString())
                //.setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(activity!!.applicationContext)
            .enqueue(oneTimeWorkRequest)
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            val toast = Toast.makeText(it, text, duration)
            toast.setGravity(Gravity.TOP, 0, 150)
            toast.show()
        }
    }
}


