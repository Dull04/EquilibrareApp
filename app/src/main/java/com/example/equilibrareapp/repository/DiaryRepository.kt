package com.example.equilibrareapp.repository

import com.example.equilibrareapp.database.DiaryDatabase
import com.example.equilibrareapp.model.Diary

class DiaryRepository(private val db: DiaryDatabase) {

    suspend fun insertDiary(diary: Diary) = db.getDiaryDao().insertDiary(diary)
    suspend fun deleteDiary(diary: Diary) = db.getDiaryDao().deleteDiary(diary)
    suspend fun updateDiary(diary: Diary) = db.getDiaryDao().updateDiary(diary)

    fun getAllDiary() = db.getDiaryDao().getAllDiary()
    fun searchDiary(query: String?) = db.getDiaryDao().searchDiary(query)
}