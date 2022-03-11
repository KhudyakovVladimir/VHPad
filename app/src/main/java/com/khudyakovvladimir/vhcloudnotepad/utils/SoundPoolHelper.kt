package com.khudyakovvladimir.vhcloudnotepad.utils

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import com.example.vhcloudnotepad.R
import javax.inject.Inject

class SoundPoolHelper @Inject constructor(application: Application) {

    private var soundPool: SoundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    private val soundWrite = soundPool.load(application.applicationContext, R.raw.write,1)
    private val soundNextPage = soundPool.load(application.applicationContext, R.raw.next_page,1)
    private val soundDeletePage = soundPool.load(application.applicationContext, R.raw.tear_a_sheet,1)
    private val soundListOrGreed = soundPool.load(application.applicationContext, R.raw.list_or_greed,1)
    private val soundPaperOnTable = soundPool.load(application.applicationContext,R.raw.paper_on_table, 1)
    private val soundCorrect = soundPool.load(application.applicationContext,R.raw.correct, 1)
    private val soundDrum = soundPool.load(application.applicationContext,R.raw.drum, 1)
    private val soundImpulse = soundPool.load(application.applicationContext,R.raw.impulse, 1)
    private val soundTrash = soundPool.load(application.applicationContext,R.raw.trash, 1)
    private val soundClick = soundPool.load(application.applicationContext,R.raw.click, 1)
    private val soundEndOfList = soundPool.load(application.applicationContext,R.raw.end_of_list, 1)
    private val soundBell = soundPool.load(application.applicationContext,R.raw.bell, 1)

    fun playSoundWrite(mute: Boolean) {
        if (!mute) {
            soundPool.play(soundWrite, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundNextPage(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundNextPage, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundDeletePage(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundDeletePage, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundListOrGreed(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundListOrGreed, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundCorrect(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundCorrect, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundDrum(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundDrum, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundImpulse(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundImpulse, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundTrash(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundTrash, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundPaperOnTable(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundPaperOnTable, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundClick(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundClick, 1F, 1F,0,0, 1F)
        }
    }

    fun playSoundEndOfList(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundEndOfList, 0.2F, 0.2F,0,0, 1F)
        }
    }

    fun playSoundBell(mute: Boolean) {
        if(!mute) {
            soundPool.play(soundBell, 0.2F, 0.2F,0,0, 1F)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(context: Context, milliseconds: Long) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.EFFECT_TICK))
    }
}