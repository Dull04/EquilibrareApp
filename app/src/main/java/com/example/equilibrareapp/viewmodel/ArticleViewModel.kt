package com.example.equilibrareapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.equilibrareapp.model.Article
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.repository.ArticleRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArticleRepository = ArticleRepository(application)
    private val preferenceHelper: PreferenceHelper = PreferenceHelper(application)

    private val _article = MutableLiveData<Article?>()
    val article: LiveData<Article?> get() = _article

    fun checkAndFetchDailyNews(apiKey: String) {
        val lastFetchDate = preferenceHelper.getLastFetchDate()
        val currentDate = getCurrentDate()

        if (lastFetchDate == null || lastFetchDate != currentDate) {
            fetchDailyNews(apiKey, currentDate)
        } else {
            // Load the article from the database if already fetched for today
            viewModelScope.launch {
                val savedArticle = repository.getSavedArticle()
                _article.postValue(savedArticle)
            }
        }
    }

    private fun fetchDailyNews(apiKey: String, currentDate: String) {
        viewModelScope.launch {
            val articleEntity = repository.getDailyNews(apiKey, "mental health tips", 1)
            articleEntity?.let {
                _article.postValue(it)
                preferenceHelper.saveLastFetchDate(currentDate)
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
