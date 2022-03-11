package com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note

class NoteDiffUtilCallback (
    private val oldList: List<Note>,
    private val newList: List<Note>
    ): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id &&
                oldList[oldItemPosition].title == newList[newItemPosition].title &&
                oldList[oldItemPosition].text == newList[newItemPosition].text &&
                oldList[oldItemPosition].time == newList[newItemPosition].time &&
                oldList[oldItemPosition].notificationId == newList[newItemPosition].notificationId &&
                oldList[oldItemPosition].bookmark == newList[newItemPosition].bookmark
    }
}