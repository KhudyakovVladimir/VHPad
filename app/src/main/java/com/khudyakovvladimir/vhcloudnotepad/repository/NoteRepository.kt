package com.khudyakovvladimir.vhcloudnotepad.repository

import androidx.lifecycle.LiveData
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.model.database.NoteDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class NoteRepository @Inject constructor(private var noteDao: NoteDao) {
    private var notes: LiveData<List<Note>>? = null
    private var noteList: ArrayList<Note>? = null
    private var note: LiveData<Note>? = null

    fun getNotesList(): ArrayList<Note>? {
        runBlocking {
            noteList = ArrayList(noteDao.getAllNotesAsList())
        }
        return noteList
    }

    fun getNotes(): LiveData<List<Note>>? {
        runBlocking {
            notes = noteDao.getAllNotesAsLiveDataListNote()
        }
        return notes
    }

    fun getNoteById(id : Int): Note? {
        var resultNote: Note? = null
        runBlocking {
            resultNote = noteDao.getNoteById(id)
        }
        return resultNote
    }

    fun getNoteByNotificationId(notificationId : Int): Note? {
        var resultNote: Note? = null
        runBlocking {
            resultNote = noteDao.getNoteByNotificationId(notificationId)
        }
        return resultNote
    }

    fun insertNote(note: Note){
        runBlocking {
            noteDao.insertNote(note)
        }
    }

    fun deleteNote(note: Note){
        runBlocking {
            noteDao.deleteNote(note)
        }
    }

    fun deleteNoteById(id: Int){
        runBlocking { this.launch {
            noteDao.deleteNoteById(id)
        } }
    }

    fun updateNote(note: Note){
        runBlocking {
            noteDao.updateNote(note)
        }
    }
}