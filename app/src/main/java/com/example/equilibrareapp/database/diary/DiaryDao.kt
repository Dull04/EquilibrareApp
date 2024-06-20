package com.example.equilibrareapp.database.diary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.equilibrareapp.model.Diary

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary)

    @Update
    suspend fun updateDiary(diary: Diary)


    @Delete
    suspend fun deleteDiary(diary: Diary)

    @Query("SELECT * FROM DIARY ORDER BY id DESC")
    fun getAllDiary(): LiveData<List<Diary>>

    @Query("SELECT * FROM DIARY WHERE noteTitle LIKE :query OR noteDesc LIKE :query")
    fun searchDiary(query: String?): LiveData<List<Diary>>

}