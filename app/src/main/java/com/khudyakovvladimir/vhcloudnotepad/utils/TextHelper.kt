package com.khudyakovvladimir.vhcloudnotepad.utils

import android.util.Log
import javax.inject.Inject

class TextHelper @Inject constructor(){

    companion object {
       const val PAPER_CLIP = 0x1F4CC
       const val DOUBLE_ARROW = 0x2195
       const val DOUBLE_ARROW_TWO = 0x21F3
       const val TIMER = 0x1F570
    }

    fun parse(data: String, symbol: Char) : List<String> {
        var finalList = mutableListOf<String>()
        try {
            val list = data.split(symbol)
            val l = list as MutableList<String>
            finalList = l
        }catch (e: Exception) {
            Log.d("TAG", "Can not parse this string")
        }
        return finalList
    }

    fun deleteEmoji(text: String, emoji: String): String {
        val stringBuilder = StringBuilder()
        val current = text.toCharArray()
        val toRemove = emoji.toCharArray()
        val v = removeSuccessive(current, toRemove)

        if (v != null) {
            for (symbol in v) {
                stringBuilder.append(symbol)
            }
        }
        return stringBuilder.toString()
    }

    private fun removeSuccessive(arr: CharArray, toRemove: CharArray): Array<String?>? {
        val res: ArrayList<String> = ArrayList()
        var i = 0
        while (i < arr.size) {
            if (arr[i] != toRemove[0]) {
                res.add(arr[i].toString())
            }
            else if (i + 1 < arr.size && arr[i + 1] != toRemove[1]) {
                res.add(arr[i].toString())
            }
            else {
                i++
            }
            i++
        }
        return res.toArray(arrayOfNulls(res.size))
    }

    fun createEmoji(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}

