package com.khudyakovvladimir.vhcloudnotepad.model.database.products

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "text", typeAffinity = ColumnInfo.TEXT)
    var text: String,

    @ColumnInfo(name = "isBought")
    var isBoought: Boolean
    )