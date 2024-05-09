package com.example.equilibrareapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.equilibrareapp.model.Diary
import com.example.equilibrareapp.repository.DiaryRepository
import kotlinx.coroutines.launch

class DiaryViewModel(app: Application, private val diaryRepository: DiaryRepository): AndroidViewModel(app) {

    fun addDiary(diary: Diary) =
        viewModelScope.launch {
            diaryRepository.insertDiary(diary)
        }

    fun deleteDiary(diary: Diary) =
        viewModelScope.launch {
            diaryRepository.deleteDiary(diary)
        }

    fun updateDiary(diary: Diary) =
        viewModelScope.launch {
            diaryRepository.updateDiary(diary)
        }

    fun getAllDiary() = diaryRepository.getAllDiary()

    fun searchDiary(query: String?) =
        diaryRepository.searchDiary(query)
}