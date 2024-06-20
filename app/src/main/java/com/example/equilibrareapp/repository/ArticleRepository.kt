package com.example.equilibrareapp.repository

import android.content.Context
import com.example.equilibrareapp.database.news.ArticleDatabase
import com.example.equilibrareapp.model.Article
import com.example.equilibrareapp.service.NewsApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(private val context: Context) {

    private val database = ArticleDatabase.getDatabase(context)
    private val articleDao = database.articleDao()
    private val service = NewsApiConfig.getApiNewsService()

    suspend fun getDailyNews(apiKey: String, query: String, pageSize: Int): Article? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getDailyNews(query, pageSize, apiKey).execute()
                if (response.isSuccessful) {
                    response.body()?.articles?.get(0)?.let { article ->
                        val articleEntity = Article(
                            url = article.url,
                            title = article.title,
                            description = article.description,
                            urlToImage = article.urlToImage,
                            publishedAt = article.publishedAt
                        )
                        articleDao.insertArticle(articleEntity)
                        return@withContext articleEntity
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            null
        }
    }

    suspend fun getSavedArticle(): Article? {
        return withContext(Dispatchers.IO) {
            articleDao.getLatestArticle()
        }
    }
}