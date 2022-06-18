package org.antilibrary.rufous.bookmark

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Query("Select * from bookmarks")
    fun getAll(): List<Bookmark>

    @Insert
    fun insert(bookmark: Bookmark)

    @Query("Delete from bookmarks")
    fun deleteAll()

    @Query("Delete FROM bookmarks WHERE siteUrl = :siteUrl")
    fun deleteBookmarkBySiteUrl(siteUrl: String)
}
