package com.example.equilibrareapp.database.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.equilibrareapp.model.Article

@Dao
interface ArticleDao{
    @Query("SELECT * FROM articles LIMIT 1")
    fun getLatestArticle(): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Article)
}