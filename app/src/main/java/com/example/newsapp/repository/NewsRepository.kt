package com.example.newsapp.repository


import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.newsapp.api.BreakingNewsDataSource
import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.api.SearchNewsDataSource
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.db.entities.Article
import kotlinx.coroutines.flow.Flow

class NewsRepository (private val database : ArticleDatabase){


     fun getAllArticles() = database.getArticleDao().getAllArticles()

    suspend fun delete(article : Article) = database.getArticleDao().delete(article)

    suspend fun upsert(article: Article) = database.getArticleDao().upsert(article)



    suspend fun getBreakingNews(countryCode: String, page : Int)
            = RetrofitInstance.api.getBreakingNews(countryCode, page)

    suspend fun searchForNews(searchQuery : String, page: Int)
            = RetrofitInstance.api.searchForNews(searchQuery, page)



    fun getBreakingPagingNews(countryCode : String): Flow<PagingData<Article>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 1
            ),
            pagingSourceFactory = {
                BreakingNewsDataSource(countryCode)
            }, initialKey = 1
        ).flow
    }


    fun searchForNewsPaging(query : String): LiveData<PagingData<Article>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 1
            ),
            pagingSourceFactory = {
                SearchNewsDataSource(query)
            }, initialKey = 1
        ).liveData
    }


}