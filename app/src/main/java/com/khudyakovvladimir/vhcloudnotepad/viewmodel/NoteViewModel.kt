package com.khudyakovvladimir.vhcloudnotepad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.repository.NoteRepository
import com.khudyakovvladimir.vhcloudnotepad.repository.ProductRepository
import com.khudyakovvladimir.vhcloudnotepad.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteViewModel @Inject constructor(
    application: Application,
    val productRepository: ProductRepository,
    val noteRepository: NoteRepository,
    val timeHelper: TimeHelper,
    val soundPoolHelper: SoundPoolHelper,
    val textHelper: TextHelper,
    val imageHelper: ImageHelper,
    val weatherHelper: WeatherHelper
) : AndroidViewModel(application) {

    private var notes: LiveData<List<Note>>? = noteRepository.getNotes()
    private var products: LiveData<List<Product>>? = productRepository.getProducts()
    var timeForTextView: MutableLiveData<String> = MutableLiveData()
    var oldNoteList: List<Note> = emptyList()


    fun getNotes(): LiveData<List<Note>>? {
        viewModelScope.launch {
            noteRepository.getNotes()
        }
        return notes
    }

    fun getNoteById(id: Int): Note? {
        return noteRepository.getNoteById(id)
    }

    fun getNoteByNotificationId(notificationId: Int): Note? {
        return noteRepository.getNoteByNotificationId(notificationId)
    }

    fun insertNote(note: Note) {
        noteRepository.insertNote(note)
    }

    fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }

    fun updateNote(note: Note) {
        noteRepository.updateNote(note)
    }

    fun startTimer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(1)
                    timeForTextView.postValue(timeHelper.getCurrentTimeForTextView())
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    //-----------------------------------------

    fun getProducts(): LiveData<List<Product>>? {
        viewModelScope.launch {
            productRepository.getProducts()
        }
        return products
    }


}