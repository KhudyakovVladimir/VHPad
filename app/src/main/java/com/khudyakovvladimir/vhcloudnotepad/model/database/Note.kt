package com.khudyakovvladimir.vhcloudnotepad.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title", typeAffinity = ColumnInfo.TEXT)
    var title: String,

    @ColumnInfo(name = "text", typeAffinity = ColumnInfo.TEXT)
    var text: String,

    @ColumnInfo(name = "time", typeAffinity = ColumnInfo.TEXT)
    var time: String,

    @ColumnInfo(name = "notificationId", typeAffinity = ColumnInfo.INTEGER)
    var notificationId: Int,

    @ColumnInfo(name = "bookmark")
    var bookmark: Boolean,

    @ColumnInfo(name = "photo")
    var photo: String
    )

