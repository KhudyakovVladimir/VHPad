package com.khudyakovvladimir.vhcloudnotepad.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.Product
import com.khudyakovvladimir.vhcloudnotepad.model.database.products.ProductDao

@Database(entities = [Note::class, Product::class], exportSchema = false, version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun productDao(): ProductDao

    companion object {
        private const val NOTE_DATABASE = "note_db"
        var instance: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase? {
            if (instance == null) {
                synchronized(this) {
                    instance =
                        Room
                            .databaseBuilder(context, NoteDatabase::class.java, NOTE_DATABASE)
                            .build()
                }
            }

            return instance
        }
    }

    fun destroyInstance() {
        instance = null
    }
}