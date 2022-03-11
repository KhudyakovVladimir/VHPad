package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.example.vhcloudnotepad.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.utils.TextHelper
import com.khudyakovvladimir.vhcloudnotepad.view.appComponent
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModel
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModelFactory
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.NoteDiffUtilCallback
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.NoteRecyclerViewAdapter
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.OnStartDragListener
import com.khudyakovvladimir.vhcloudnotepad.widgets.recyclerview.itemtouchhelper.SimpleItemTouchHelperCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class MainScreen : Fragment(), OnStartDragListener {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteViewModelFactory: NoteViewModelFactory

    @Inject
    lateinit var factory: NoteViewModelFactory.Factory

    private lateinit var recyclerView: RecyclerView
    private lateinit var noteRecyclerViewAdapter: NoteRecyclerViewAdapter
    private lateinit var switch: Switch
    private lateinit var floatingActionButtonMainScreen: FloatingActionButton
    private var mItemTouchHelper: ItemTouchHelper? = null

    var mute: Boolean = false
    var language: String? = ""
    var adapterPosition: Int = 0
    var switchPosition: Boolean = true
    private var lastAdapterPosition: Int = 1

    private lateinit  var sharedPreferences: SharedPreferences
    private var codeForMenu: Int = 1
    private var codeForLayoutManager: Int = 1
    private var note: Note? = null

    lateinit var city: String
    lateinit var countryCode: String

    val CODE_FOR_LAYOUT_MANAGER = "codeForLayoutManager"
    val CODE_FOR_MENU = "codeForMenu"

    override fun onAttach(context: Context) {
        context.appComponent.injectMainScreen(this)
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("mute")) {
            mute = sharedPreferences.getBoolean("mute", false)
        }

        if(sharedPreferences.contains("language")) {
            language = sharedPreferences.getString("language", "")
        }
        if(sharedPreferences.contains("adapterPosition")) {
            adapterPosition = sharedPreferences.getInt("adapterPosition", 0)
        }
        if(sharedPreferences.contains("switchMainScreenPosition")) {
            switchPosition = sharedPreferences.getBoolean("switchMainScreenPosition", true)
        }
        if (sharedPreferences.contains("city")) {
            city = sharedPreferences.getString("city", "").toString()
            if(sharedPreferences.contains("countryCode")) {
                countryCode = sharedPreferences.getString("countryCode", "").toString()
            }
        }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_screen_layout, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModelFactory = factory.createNoteViewModelFactory(activity?.application!!)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory).get(NoteViewModel::class.java)

        noteViewModel.startTimer()

        switch = view.findViewById(R.id.switch1)
        floatingActionButtonMainScreen = view.findViewById(R.id.floatingActionButtonMainScreen)

        when(language) {
            "eng" -> {
                val unicode = 0x1F4C4
                val textEmoji = String(Character.toChars(unicode))
                switch.text = textEmoji
            }
            "rus" -> {
                val unicode = 0x1F4C4
                val textEmoji = String(Character.toChars(unicode))
                switch.text = textEmoji
            }
            "other" -> {

            }
        }

        noteViewModel.getNotes()?.observe(this) {
            val oldList = noteRecyclerViewAdapter.listNotes
            val newList = it
            val noteDiffUtilCallback = NoteDiffUtilCallback(oldList, newList)
            val diffResult = DiffUtil.calculateDiff(noteDiffUtilCallback)

            noteRecyclerViewAdapter.updateAdapter(it)
            diffResult.dispatchUpdatesTo(noteRecyclerViewAdapter)

            noteViewModel.oldNoteList = it

            setLastAdapterElementPosition()

            //Log.d("TAG", "MainScreen - notes = $it")
        }

        val textViewWeatherMainScreen = view.findViewById<TextView>(R.id.textViewWeatherMainScreen)
        val imageViewWeatherMainScreen = view.findViewById<ImageView>(R.id.imageViewWeatherMainScreen)
        imageViewWeatherMainScreen.setOnClickListener {
            findNavController().navigate(R.id.weather)
        }

        noteViewModel.weatherHelper.getWeather(activity!!.applicationContext, imageViewWeatherMainScreen, textViewWeatherMainScreen, city, countryCode)

        recyclerView = view.findViewById(R.id.recyclerView)!!
        recyclerView.itemAnimator?.changeDuration = 0

        checkCodeFor(CODE_FOR_LAYOUT_MANAGER)

        when(codeForLayoutManager) {
            1 -> { recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext) }
            2 -> { recyclerView.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) }
        }

        val itemClick = { note: Note -> navigateToSingleNote(note) }
        val itemLongClick = { note: Note -> setMenuCode(note, 3) }
        val itemSwipeDelete = { note: Note -> deleteNote(note) }
        val itemLongClickMove = { noteFrom: Note, noteTo: Note -> synchronizeDraggedNotesToDatabase(noteFrom, noteTo) }

        noteRecyclerViewAdapter =
            NoteRecyclerViewAdapter(
                activity!!.applicationContext,
                ArrayList(emptyList()),
                this,
                itemClick,
                itemLongClick,
                itemSwipeDelete,
                itemLongClickMove
            )

        recyclerView.adapter = noteRecyclerViewAdapter

        //________________________________________ItemTouchHelper
        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(noteRecyclerViewAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper!!.attachToRecyclerView(recyclerView)
        //________________________________________

        lifecycleScope.launch {
            delay(100)
            recyclerView.scrollToPosition(adapterPosition)
        }

        switch.isChecked = switchPosition
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(isChecked) {
                true -> {
                    val v = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK))
                    floatingActionButtonMainScreen.visibility = View.VISIBLE
                    floatingActionButtonMainScreen.isClickable = true
                    val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("switchMainScreenPosition", true)
                    editor.apply()
                }
                false -> {
                    val v = activity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.EFFECT_TICK))
                    floatingActionButtonMainScreen.visibility = View.INVISIBLE
                    floatingActionButtonMainScreen.isClickable = false
                    val sharedPreferences = activity?.applicationContext!!.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putBoolean("switchMainScreenPosition", false)
                    editor.apply()
                }
            }
        }

        when(switchPosition) {
            true -> { floatingActionButtonMainScreen.visibility = View.VISIBLE }
            false -> { floatingActionButtonMainScreen.visibility = View.INVISIBLE }
        }

        floatingActionButtonMainScreen.setOnClickListener {
            if(sharedPreferences.contains("adapterPosition")) {
                flagSaveAdapterPosition = false
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", 1)
                editor.apply()
            }
            findNavController().navigate(R.id.newNote)
        }

        noteViewModel.timeForTextView.observe(this) {
            view.findViewById<TextView>(R.id.textViewMainScreen).text = it
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noteViewModel.soundPoolHelper.vibrate(activity!!.applicationContext, 2)
                    noteViewModel.soundPoolHelper.playSoundImpulse(mute)

                }
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    noteViewModel.soundPoolHelper.vibrate(activity!!.applicationContext, 2)
                    noteViewModel.soundPoolHelper.playSoundImpulse(mute)
                }
            }
        })
    }

    //____________________________________________________________________________________________________

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        checkCodeFor(CODE_FOR_MENU)

        when(codeForMenu) {
            1 -> { inflater.inflate(R.menu.menu_layout, menu) }
            2 -> { inflater.inflate(R.menu.menu_layout_two, menu) }
            3 -> { inflater.inflate(R.menu.menu_layout_three, menu) }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.grid -> {
                noteViewModel.soundPoolHelper.playSoundListOrGreed(mute)
                setGrid()
                codeForMenu = 2
                activity?.invalidateOptionsMenu()
            }
            R.id.list -> {
                noteViewModel.soundPoolHelper.playSoundListOrGreed(mute)
                setLinear()
                codeForMenu = 1
                activity?.invalidateOptionsMenu()
            }
            R.id.delete -> {
                deleteNote(note!!)
                val check = checkCodeFor(CODE_FOR_LAYOUT_MANAGER)
                savePreference(check)
                activity?.invalidateOptionsMenu()
            }

            R.id.hold -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if(!note!!.bookmark) {
                        val textEmoji = noteViewModel.textHelper.createEmoji(TextHelper.PAPER_CLIP)
                        noteViewModel.insertNote(Note(note!!.id, textEmoji  + note!!.title, note!!.text, noteViewModel.timeHelper.getCurrentTime(), note!!.notificationId, true, note!!.photo))
                    }else {
                        val titleText = note!!.title
                        if(titleText != "") {
                            val resultText = noteViewModel.textHelper.deleteEmoji(titleText, noteViewModel.textHelper.createEmoji(TextHelper.PAPER_CLIP))
                            noteViewModel.insertNote(Note(note!!.id, resultText, note!!.text, noteViewModel.timeHelper.getCurrentTime(), note!!.notificationId, false, note!!.photo))
                        }else {
                            noteViewModel.insertNote(Note(note!!.id, note!!.title, note!!.text, noteViewModel.timeHelper.getCurrentTime(), note!!.notificationId, false, note!!.photo))
                        }

                    }

                    val check = checkCodeFor(CODE_FOR_LAYOUT_MANAGER)
                    savePreference(check)
                    activity?.invalidateOptionsMenu()
                }
            }

            R.id.remind -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val textEmoji = noteViewModel.textHelper.createEmoji(TextHelper.TIMER)
                    noteViewModel.insertNote(Note(note!!.id, textEmoji + note!!.title, note!!.text, noteViewModel.timeHelper.getCurrentTime(), note!!.notificationId, note!!.bookmark, note!!.photo))
                }
                val check = checkCodeFor(CODE_FOR_LAYOUT_MANAGER)

                savePreference(check)
                activity?.invalidateOptionsMenu()

                noteViewModel.soundPoolHelper.playSoundBell(mute)

                findNavController().navigate(R.id.notification)
            }

            R.id.back -> {
                val check = checkCodeFor(CODE_FOR_LAYOUT_MANAGER)
                savePreference(check)
                activity?.invalidateOptionsMenu()
            }
        }
        return true
    }

    //____________________________________________________________________________________________

    private fun navigateToSingleNote(_note: Note) {
        noteViewModel.soundPoolHelper.playSoundNextPage(mute)
        val bundle = Bundle()
        bundle.putInt("noteId", _note.id)

        if(codeForMenu ==  3) {
            codeForMenu = checkCodeFor(CODE_FOR_LAYOUT_MANAGER)
        }

        savePreference(codeForMenu)
        activity!!.invalidateOptionsMenu()
        findNavController().navigate(R.id.newNote, bundle)
    }

    private fun setGrid(): Boolean {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        savePreference(2)
        return true
    }

    private fun setLinear(): Boolean {
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        savePreference(1)
        return true
    }

    private fun setMenuCode(_note: Note, code: Int): Boolean {
        codeForMenu = code
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("codeForMenu", code)
        editor.putInt("noteIdForNotification", _note.id)
        editor.putString("noteTitleForNotification", _note.title)
        editor.putString("noteTextForNotification", _note.text)
        editor.putInt("notificationId", _note.notificationId)
        editor.apply()

        note = _note
        activity?.invalidateOptionsMenu()
        return true
    }

    private fun savePreference(code: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("codeForLayoutManager", code)
        editor.putInt("codeForMenu", code)
        editor.apply()
    }

    private fun checkCodeFor(entity: String): Int {
        if (sharedPreferences.contains(entity))
        {
            if(entity == CODE_FOR_MENU) {
                codeForMenu = sharedPreferences.getInt(entity, 1)
                return codeForMenu
            }
            if(entity == CODE_FOR_LAYOUT_MANAGER) {
                codeForLayoutManager = sharedPreferences.getInt(entity, 1)
                return codeForLayoutManager
            }
            if(entity == "noteIdForNotification") {
                return sharedPreferences.getInt(entity, 1)
            }
        }
        return 1
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper?.startDrag(viewHolder)
    }

    private fun deleteNote(_note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            if(noteViewModel.noteRepository.getNotesList()?.size!! > 1) {
                deletePhotoFromFileSystem(_note)
                noteViewModel.deleteNote(_note)
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            }else if (noteViewModel.noteRepository.getNotesList()?.size!! == 1) {
                _note.title = ""
                _note.text = ""
                _note.time = ""
                _note.bookmark = false
                _note.photo = ""
                noteViewModel.insertNote(_note)
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            }
        }
    }

    private fun synchronizeDraggedNotesToDatabase(_noteFrom: Note, _noteTo: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentList = noteViewModel.noteRepository.getNotesList()
            val updatedList = noteViewModel.noteRepository.getNotesList()

            var indexFrom = 0
            var indexTo = 0

            for (note in updatedList!!) {
                if(note.id == _noteFrom.id) {
                    break
                }
                indexFrom++
            }

            for (note in updatedList) {
                if(note.id == _noteTo.id) {
                    break
                }
                indexTo++
            }

            val tempNote = updatedList[indexFrom]
            updatedList.removeAt(indexFrom)
            updatedList.add(indexTo, tempNote)

            //__________update database
            for (note in updatedList.indices) {
                val currentNote = currentList?.get(note)
                val updatedNote = updatedList[note]
                noteViewModel.noteRepository.updateNote(Note(currentNote!!.id, updatedNote.title, updatedNote.text, updatedNote.time , updatedNote.notificationId, updatedNote.bookmark, updatedNote.photo))
            }
        }
    }

    private fun setLastAdapterElementPosition() {
        CoroutineScope(Dispatchers.IO).launch {
            val lastPosition = noteViewModel.noteRepository.getNotesList()?.size?.minus(1)
            lastAdapterPosition = lastPosition!!
        }
    }

    private fun deletePhotoFromFileSystem(note: Note?) {
        val path = note!!.photo
        val file = File(path)

        if (file.exists()) {
            if(file.delete()) {
                Log.d("TAG", "FILE DELETED SUCCESSFULLY")
            }
        }else{
            Log.d("TAG", "FILE is not available")
        }
    }

    override fun onPause() {
        super.onPause()
        if(flagSaveAdapterPosition) {
            if(recyclerView.layoutManager is LinearLayoutManager) {
                val position = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position)
                editor.apply()
            }
            if(recyclerView.layoutManager is StaggeredGridLayoutManager) {
                val array = IntArray((recyclerView.layoutManager as StaggeredGridLayoutManager).spanCount)
                val position = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(array)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position[0])
                editor.apply()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(flagSaveAdapterPosition) {
            if(recyclerView.layoutManager is LinearLayoutManager) {
                val position = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                Log.d("TAG", "LINEAR - position = $position")
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("adapterPosition", position)
                editor.apply()
            }
        }
        flagSaveAdapterPosition = true
    }

    companion object {
        var flagSaveAdapterPosition: Boolean = true
    }
}


