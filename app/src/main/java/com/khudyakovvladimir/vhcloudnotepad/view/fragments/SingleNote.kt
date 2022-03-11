package com.khudyakovvladimir.vhcloudnotepad.view.fragments

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.vhcloudnotepad.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.utils.TextHelper
import com.khudyakovvladimir.vhcloudnotepad.view.appComponent
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModel
import com.khudyakovvladimir.vhcloudnotepad.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class SingleNote : Fragment() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteViewModelFactory: NoteViewModelFactory

    @Inject
    lateinit var factory: NoteViewModelFactory.Factory

    lateinit var buttonClear: Button
    lateinit var buttonSave: Button
    lateinit var buttonDelete: Button
    lateinit var editTextTitle: EditText
    lateinit var editTextText: EditText
    lateinit var floatingActionButtonTakeAPhoto: FloatingActionButton
    lateinit var floatingActionButtonShopping: FloatingActionButton
    lateinit var imageView: ImageView

    private lateinit  var sharedPreferences: SharedPreferences
    private var note: Note? = null
    var noteId: Int? = null
    var notePhoto: String = ""
    var noteNotificationId: Int = 0
    var notificationIdGlobal: Int = 0
    var codeForMenuSingleNote: Int = 1
    var mute: Boolean = false
    var language: String? = ""
    var textSize: String? = ""
    var doNotSave: Boolean = true
    var flagForImageView: Boolean = true
    lateinit var photoUri: Uri
    var photoNameForIntent = ""

    override fun onAttach(context: Context) {
        context.appComponent.injectSingleNote(this)
        sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("mute")) {
            mute = sharedPreferences.getBoolean("mute", false)
        }
        if(sharedPreferences.contains("language")) {
            language = sharedPreferences.getString("language", "")
        }
        if(sharedPreferences.contains("textSize")) {
            textSize = sharedPreferences.getString("textSize", "")
        }
        if(sharedPreferences.contains("codeForMenuSingleNote")) {
            codeForMenuSingleNote = sharedPreferences.getInt("codeForMenuSingleNote", 1)
        }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                noteViewModel.soundPoolHelper.playSoundWrite(mute)
                saveNote(true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_note_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClear = view.findViewById(R.id.buttonClear)
        buttonSave = view.findViewById(R.id.buttonSave)
        buttonDelete = view.findViewById(R.id.buttonDelete)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextText = view.findViewById(R.id.editTextText)
        imageView = view.findViewById(R.id.imageViewPhoto)
        floatingActionButtonTakeAPhoto = view.findViewById(R.id.floatingActionButtonSingleNote)
        floatingActionButtonShopping = view.findViewById(R.id.floatingActionButtonShopping)

        when(language) {
            "eng" -> {
                buttonClear.text = "clear"
                buttonSave.text = "save"
                buttonDelete.text = "delete"
                editTextTitle.hint = "Title"
                editTextText.hint = "Text"
            }
            "rus" -> {
                buttonClear.text = "стереть"
                buttonSave.text = "сохранить"
                buttonDelete.text = "удалить"
                editTextTitle.hint = "Заголовок"
                editTextText.hint = "Текст"
            }
            "other" -> {

            }
        }

        when(textSize) {
            "small" -> {
                editTextTitle.textSize = 15F
                editTextText.textSize = 12F
            }
            "medium" -> {
                editTextTitle.textSize = 18F
                editTextText.textSize = 15F
            }
            "large" -> {
                editTextTitle.textSize = 23F
                editTextText.textSize = 20F
            }
        }

        when(codeForMenuSingleNote) {
            1 -> {
                floatingActionButtonTakeAPhoto.visibility = View.VISIBLE
                floatingActionButtonShopping.visibility = View.VISIBLE
            }
            2 -> {
                floatingActionButtonTakeAPhoto.visibility = View.INVISIBLE
                floatingActionButtonShopping.visibility = View.INVISIBLE
            }
        }

        setInputTypeForEditText()

        floatingActionButtonTakeAPhoto.setOnClickListener {
            takePhoto()
        }

        floatingActionButtonShopping.setOnClickListener {
            val textHelper = TextHelper()
            val parseList = textHelper.parse(editTextText.text.toString(), ',')
            val size = parseList.size

            CoroutineScope(Dispatchers.IO).launch {
                noteViewModel.productRepository.deleteAllProducts()
                for (i in 1..size) {
                    noteViewModel.productRepository.insertProduct(Product(i, parseList[i - 1], false))
                }
            }
            makeToast("shopping list created")
        }

        imageView.setOnClickListener {
            imageView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            if(flagForImageView) {
                Log.d("TAG", "flag true")
                imageView.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                flagForImageView = false
            }else {
                Log.d("TAG", "flag false")
                imageView.layoutParams.width = 600
                imageView.layoutParams.height = 600
                flagForImageView = true
                val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayoutSingleNote)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(R.id.imageViewPhoto, ConstraintSet.RIGHT, R.id.editTextTitle, ConstraintSet.RIGHT, 10)
                constraintSet.connect(R.id.imageViewPhoto, ConstraintSet.TOP, R.id.editTextTitle, ConstraintSet.TOP, 280)
                constraintSet.applyTo(constraintLayout)
            }
        }

        noteViewModelFactory = factory.createNoteViewModelFactory(activity?.application!!)
        noteViewModel = ViewModelProvider(this, noteViewModelFactory).get(NoteViewModel::class.java)

        buttonClear.setOnClickListener {
            editTextTitle.setText("")
            editTextText.setText("")
        }

        buttonSave.setOnClickListener {
            buttonSave.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            noteViewModel.soundPoolHelper.playSoundWrite(mute)
            saveNote(true)
        }

        buttonDelete.setOnClickListener {
            noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            buttonDelete.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            deleteNote()
        }

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationManager.activeNotifications.isNotEmpty()) {
            val notificationId = notificationManager.activeNotifications[0].id
            notificationManager.cancel(notificationId)
        }

        //this bundle from notification

        var id = arguments?.getInt("noteId",0)
        val notificationId = arguments?.getInt("notificationId", 0)

        if(id == 0) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deferredId = async { noteViewModel.getNoteByNotificationId(notificationId!!) }
                    val newNote = deferredId.await()
                    val newId = newNote?.id
                    val newTitle = newNote?.title
                    id = newId
                    note = newNote
                    noteId = newId
                    notificationIdGlobal = notificationId!!
                    noteNotificationId = notificationId

                    var resultText = " "

                    resultText = noteViewModel.textHelper.deleteEmoji(newTitle!!, noteViewModel.textHelper.createEmoji(TextHelper.TIMER))

                    CoroutineScope(Dispatchers.Main).launch {
                        editTextTitle.setText(resultText)
                        editTextText.setText(newNote?.text)

                        if(note!!.photo != "") {
                            imageView.visibility = View.VISIBLE
                            noteViewModel.imageHelper.loadImageFromStorage(note!!.photo, imageView, imageView.id)
                        }

                    }
                }catch (e: Exception) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val list = noteViewModel.noteRepository.getNotesList()
                        val size = list?.size
                        val lastNote = list?.get(size!! - 1)
                        val lastNoteId = lastNote?.id
                        val newNoteId = lastNoteId!! + 1
                        noteId = newNoteId
                        note = Note(newNoteId, "", "", noteViewModel.timeHelper.getCurrentTime(), newNoteId, false, "")
                    }
                    editTextTitle.setText("")
                    editTextText.setText("")
                }
            }
        }

        if (id == null) {
            CoroutineScope(Dispatchers.IO).launch {
                val list = noteViewModel.noteRepository.getNotesList()
                val size = list?.size
                val lastNote = list?.get(size!! - 1)
                val lastNoteId = lastNote?.id
                val newNoteId = lastNoteId!! + 1
                noteId = newNoteId
                note = Note(newNoteId, "", "", noteViewModel.timeHelper.getCurrentTime(), newNoteId, false, "")
            }
            editTextTitle.setText("")
            editTextText.setText("")
        }
        else if (id != 0) {
            var tempNote = Note(100, "100", "100", "100", 100, false, "")
            CoroutineScope(Dispatchers.IO).launch {
                tempNote = noteViewModel.getNoteById(id!!)!!
                note = tempNote
                noteId = id
                noteNotificationId = tempNote.notificationId
                CoroutineScope(Dispatchers.Main).launch {
                    editTextTitle.setText(tempNote.title)
                    editTextText.setText(tempNote.text)

                    if(note!!.photo != "") {
                        imageView.visibility = View.VISIBLE
                        noteViewModel.imageHelper.loadImageFromStorage(note!!.photo, imageView, imageView.id)
                    }else {
                        imageView.visibility = View.INVISIBLE
                    }
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(doNotSave) {
            saveNote(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(doNotSave) {
            saveNote(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            imageView.visibility = View.VISIBLE
            noteViewModel.imageHelper.loadImageFromStorage(note!!.photo, imageView, imageView.id)
        }
    }
    //--------------------------------------------------------------

    private fun addNoteToList(id: Int, title: String, text: String, time: String, notificationId: Int, bookmark: Boolean, photo: String) {
        var tempId = notificationId
        var tempBookmark = bookmark
        CoroutineScope(Dispatchers.IO).launch {
            if(notificationId == 0) {
                tempId = id
            }
            noteViewModel.insertNote(Note(id, title, text, noteViewModel.timeHelper.getCurrentTime(), tempId, bookmark, photo))
        }
    }

    private fun delNoteFromList(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val deferredId = async { noteViewModel.getNoteById(id) }
            val _note = deferredId.await()
            if(noteViewModel.noteRepository.getNotesList()?.size!! > 1) {
                noteViewModel.deleteNote(_note!!)
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
            }else if (noteViewModel.noteRepository.getNotesList()?.size!! == 1) {
                _note?.title = ""
                _note?.text = ""
                _note?.time = ""
                noteViewModel.insertNote(_note!!)
            }
        }
    }

    private fun saveNote(goToMainScreen: Boolean) {
        if(editTextTitle.text.toString() != "" || editTextText.text.toString() != "") {
            addNoteToList(noteId!!, editTextTitle.text.toString(), editTextText.text.toString(), noteViewModel.timeHelper.getCurrentTime(), noteNotificationId, note!!.bookmark, note!!.photo)
        }

        if(goToMainScreen) {
            findNavController().navigate(R.id.notes)
        }
    }

    private fun deleteNote() {
        doNotSave = false
        if (editTextText.text.toString() != "") {
            deletePhotoFromFileSystem(note!!)
            delNoteFromList(noteId!!)
        }
        findNavController().navigate(R.id.notes)
    }

    private fun setInputTypeForEditText() {
        editTextTitle.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        editTextText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    private fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        activity?.let {
            val toast = Toast.makeText(it, text, duration)
            toast.setGravity(Gravity.TOP, 0, 150)
            toast.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when(codeForMenuSingleNote) {
            1 -> { inflater.inflate(R.menu.menu_single_note_one, menu) }
            2 -> { inflater.inflate(R.menu.menu_single_note_two, menu) }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.clear -> {
                editTextTitle.setText("")
                editTextText.setText("")
            }
            R.id.save-> {
                buttonSave.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                noteViewModel.soundPoolHelper.playSoundWrite(mute)
                saveNote(false)
            }
            R.id.delete -> {
                noteViewModel.soundPoolHelper.playSoundDeletePage(mute)
                buttonDelete.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                deleteNote()
            }
            R.id.remind -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val textEmoji = noteViewModel.textHelper.createEmoji(TextHelper.TIMER)
                    noteViewModel.insertNote(Note(note!!.id, textEmoji + note!!.title, note!!.text, noteViewModel.timeHelper.getCurrentTime(), note!!.notificationId, note!!.bookmark, note!!.photo))
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val emoji = noteViewModel.textHelper.createEmoji(TextHelper.TIMER)
                    val currentText = editTextTitle.text
                    editTextTitle.setText("$emoji$currentText")
                }

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("noteIdForNotification", note!!.id)
                editor.putString("noteTitleForNotification", note!!.title)
                editor.putString("noteTextForNotification", note!!.text)
                editor.putInt("notificationId", note!!.notificationId)
                editor.apply()

                noteViewModel.soundPoolHelper.playSoundBell(mute)

                findNavController().navigate(R.id.notification)
            }
            R.id.fastAccessButtonsOn -> {
                floatingActionButtonTakeAPhoto.visibility = View.INVISIBLE
                floatingActionButtonTakeAPhoto.isClickable = false
                floatingActionButtonShopping.visibility = View.INVISIBLE
                floatingActionButtonShopping.isClickable = false
                imageView.visibility = View.INVISIBLE
                codeForMenuSingleNote = 2
                activity?.invalidateOptionsMenu()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("codeForMenuSingleNote", codeForMenuSingleNote)
                editor.apply()
            }
            R.id.fastAccessButtonsOff -> {
                floatingActionButtonTakeAPhoto.visibility = View.VISIBLE
                floatingActionButtonTakeAPhoto.isClickable = true
                floatingActionButtonShopping.visibility = View.VISIBLE
                floatingActionButtonShopping.isClickable = true
                if(note?.photo != "") {
                    imageView.visibility = View.VISIBLE
                }
                codeForMenuSingleNote = 1
                activity?.invalidateOptionsMenu()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("codeForMenuSingleNote", codeForMenuSingleNote)
                editor.apply()
            }
            R.id.deletePhoto -> {
                CoroutineScope(Dispatchers.IO).launch {
                    deletePhotoFromFileSystem(note)
                    note!!.photo = ""
                    noteViewModel.insertNote(Note(note!!.id, note!!.title, note!!.text, note!!.time, note!!.notificationId, note!!.bookmark, ""))
                    imageView.visibility = View.INVISIBLE

                }
            }
            R.id.photo -> {
                takePhoto()
            }
            R.id.shopping -> {
                val textHelper = TextHelper()
                val parseList = textHelper.parse(editTextText.text.toString(), ',')
                val size = parseList.size

                CoroutineScope(Dispatchers.IO).launch {
                    noteViewModel.productRepository.deleteAllProducts()
                    for (i in 1..size) {
                        noteViewModel.productRepository.insertProduct(Product(i, parseList[i - 1], false))
                    }
                }
                makeToast("shopping list created")
            }
        }
        return true
    }

    private fun deletePhotoFromFileSystem(note: Note?) {
        val path = note!!.photo
        val file = File(path)
        Log.d("TAG", "FILE to be deleted PATH = $path")
        if (file.exists()) {
            if(file.delete()) {
                Log.d("TAG", "FILE DELETED SUCCESSFULLY")
            }
        }else{
            Log.d("TAG", "FILE is not available")
        }
    }

    private fun takePhoto() {

        if(note!!.photo != "") {
            Log.d("TAG", "takePhoto() - note.photo = ${note!!.photo}")
            deletePhotoFromFileSystem(note)
        }

        val path = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val appFolder = File(path, "VHNotepad")

        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }else {
        }

        val name = noteViewModel.timeHelper.getCurrentTimeForTextView()
        photoNameForIntent = name

        val file = noteViewModel.imageHelper.getFile(name, appFolder)
        noteViewModel.imageHelper.saveEmptyFile(name, appFolder)
        photoUri = FileProvider.getUriForFile(context?.applicationContext!!, "com.khudyakovvladimir.vhcloudnotepad.utils.MyFileProvider", file)

        val filesInFolder = appFolder.listFiles()
        val lastFilePath = filesInFolder[filesInFolder.size - 1].absolutePath

        notePhoto = lastFilePath
        note!!.photo = file.absolutePath

        CoroutineScope(Dispatchers.IO).launch {
            noteViewModel.insertNote(Note(note!!.id, note!!.title, note!!.text, note!!.time, note!!.notificationId, note!!.bookmark, note!!.photo))
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, 1)
    }
}