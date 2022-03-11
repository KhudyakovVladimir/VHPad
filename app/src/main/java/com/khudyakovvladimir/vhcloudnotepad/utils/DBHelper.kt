package com.khudyakovvladimir.vhcloudnotepad.utils

import android.content.Context
import com.khudyakovvladimir.vhcloudnotepad.model.database.Note
import com.khudyakovvladimir.vhcloudnotepad.model.database.NoteDatabase
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBHelper(val context: Context) {

    lateinit var noteDatabase: NoteDatabase

    fun createDatabase() {
        noteDatabase = NoteDatabase.getInstance(context)!!
        CoroutineScope(Dispatchers.IO).launch {
            noteDatabase.noteDao().insertNote(Note(1,"title ", "text", "", 1, false, ""))
            noteDatabase.productDao().insertProduct(Product(1, "product", false))
        }
    }
}