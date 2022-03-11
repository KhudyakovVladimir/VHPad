package com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.TextView
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vhcloudnotepad.R
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.ItemTouchHelperAdapter
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.ItemTouchHelperViewHolder
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.OnStartDragListener
import java.util.*

class NoteRecyclerViewAdapter(
    val context: Context,
    var listNotes: List<Note>,
    dragStartListener: OnStartDragListener,
    private val itemClick: (note: Note) -> Unit,
    private val itemLongClick: (note: Note) -> Boolean,
    private val itemSwipeDelete: (note: Note) -> Unit,
    private val itemLongClickMove: (noteFrom: Note, noteTo: Note) -> Unit,
): RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchHelperAdapter {

    private var newList: List<Note> = emptyList()
    private var flag: Boolean = false
    private val mDragStartListener: OnStartDragListener = dragStartListener

    //_____________________________________NoteHolder(Without Title)_________________________________________________
    inner class NoteHolder(itemView: View): RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        lateinit var textViewText: TextView
        lateinit var textViewTime: TextView
        lateinit var textViewDrag: TextView

        fun bind(note: Note) {
            textViewText = itemView.findViewById(R.id.textViewText)
            textViewTime = itemView.findViewById(R.id.textViewTime)
            textViewDrag = itemView.findViewById(R.id.textViewDrag)

            if(note.text.length > 82) {
                textViewText.text = note.text.substring(0, 80)
            }else {
                textViewText.text = note.text
            }

            textViewTime.text = note.time

            itemView.setOnClickListener {
                itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                itemClick(note)
            }

            itemView.setOnLongClickListener {
                itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                itemLongClick(note)
            }
        }

        override fun onItemSelected() {
            textViewText.setBackgroundResource(R.drawable.rounded_corner_title_selected)
            textViewTime.setBackgroundResource(R.drawable.rounded_corner_time_selected)
            textViewDrag.setBackgroundResource(R.drawable.rounded_corner_drag_selected)
        }

        override fun onItemClear() {
            textViewText.setBackgroundResource(R.drawable.rounded_corner_title)
            textViewTime.setBackgroundResource(R.drawable.rounded_corner_time)
            textViewDrag.setBackgroundResource(R.drawable.rounded_corner_drag)

            if (flag) {
                itemLongClickMove(newList[0], newList[1])
            }
            flag = false

        }
    }

    //_____________________________________NoteHolder_________________________________________________

    //_____________________________________NoteHolderTwo(With Title)_________________________________________________
    inner class NoteHolderTwo(itemView: View): RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {

        lateinit var textViewTitle: TextView
        lateinit var textViewText: TextView
        lateinit var textViewTime: TextView
        lateinit var textViewDrag: TextView

        fun bind(note: Note) {
            textViewTitle = itemView.findViewById(R.id.textViewTitle)
            textViewText = itemView.findViewById(R.id.textViewText)
            textViewTime = itemView.findViewById(R.id.textViewTime)
            textViewDrag = itemView.findViewById(R.id.textViewDrag)

            textViewTitle.text = note.title

            if(note.text.length > 100) {
                textViewText.text = "${note.text.substring(0, 90)} ..."
            }else {
                textViewText.text = note.text
            }

            textViewTime.text = note.time

            itemView.setOnClickListener {
                itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                itemClick(note)
            }

            itemView.setOnLongClickListener {
                itemView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                itemLongClick(note)
            }
        }

        override fun onItemSelected() {
            textViewTitle.setBackgroundResource(R.drawable.rounded_corner_title_selected)
            textViewText.setBackgroundResource(R.drawable.rounded_corner_text_selected)
            textViewTime.setBackgroundResource(R.drawable.rounded_corner_time_selected)
            textViewDrag.setBackgroundResource(R.drawable.rounded_corner_drag_selected)
        }

        override fun onItemClear() {
            textViewTitle.setBackgroundResource(R.drawable.rounded_corner_title)
            textViewText.setBackgroundResource(R.drawable.rounded_corner_text)
            textViewTime.setBackgroundResource(R.drawable.rounded_corner_time)
            textViewDrag.setBackgroundResource(R.drawable.rounded_corner_drag)

            if (flag) {
                itemLongClickMove(newList[0], newList[1])
            }
            flag = false

        }
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    fun updateAdapter(_listNotes: List<Note>) {
        this.listNotes.apply {
            listNotes = emptyList()
            val tempList = listNotes.toMutableList()
            val tempList2 = _listNotes.toMutableList()
            tempList.addAll(tempList2)
            val resultList = tempList2.toList()
            listNotes = resultList
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(listNotes[position].title) {
            "" -> {
                0
            }
            else -> {
                1
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        newList =  getNewList(listNotes[fromPosition], listNotes[toPosition])
        Collections.swap(listNotes, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        return true
    }

    override fun onItemDismiss(position: Int) {
        val note = listNotes[position]
        itemSwipeDelete(listNotes[position])

        val tempList = ArrayList(listNotes)
        tempList.removeAt(position)
        val list = tempList as List<Note>
        listNotes = list

        flag = false

        notifyItemRemoved(position)
    }

    private fun getNewList(note1: Note, note2: Note): List<Note> {
        flag = true
        return listOf(note1, note2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        val viewHolder: RecyclerView.ViewHolder?

        if (viewType == 0) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_note_item_two, parent, false)
            viewHolder = NoteHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_note_item, parent, false)
            viewHolder = NoteHolderTwo(view)
        }

        return viewHolder
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            0 -> {
                val noteHolder = holder as NoteHolder
                noteHolder.bind(listNotes[position])

                noteHolder.textViewDrag.setOnTouchListener { v, event ->
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder)
                    }
                    false
                }
            }

            1 -> {
                val noteHolder = holder as NoteHolderTwo
                noteHolder.bind(listNotes[position])

                noteHolder.textViewDrag.setOnTouchListener { v, event ->
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder)
                    }
                    false
                }
            }

            2 -> {
                //bookmark
            }
        }
    }
}