package org.antilibrary.rufous.bookmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark (
    @ColumnInfo var siteName: String,
    @ColumnInfo var siteUrl: String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}