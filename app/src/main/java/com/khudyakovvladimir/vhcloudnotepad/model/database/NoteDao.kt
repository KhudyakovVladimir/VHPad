package com.khudyakovvladimir.vhcloudnotepad.model.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY bookmark DESC, id DESC")
    fun getAllNotesAsLiveDataListNote(): LiveData<List<Note>>?

    @Query("SELECT * FROM notes")
    fun getAllNotesAsList(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Note

    @Query("SELECT * FROM notes WHERE notificationId = :notificationId")
    fun getNoteByNotificationId(notificationId: Int): Note

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteByIdAsLiveData(id: Int): LiveData<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteNoteById(id: Int)

    @Update
    fun updateNote(note: Note)
}