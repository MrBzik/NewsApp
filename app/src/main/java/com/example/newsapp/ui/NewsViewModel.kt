package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.NewsApplication
import com.example.newsapp.db.entities.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.ui.fragments.BreakingNewsFragment
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel (private val repository : NewsRepository,
                   app : Application
        ) : AndroidViewModel(app) {

    var breakingNewsPage = 1

    var searchNewsPage = 1

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    val sharedPref = app.getSharedPreferences("Country", Context.MODE_PRIVATE)

    val editor = sharedPref.edit()

    fun getCountryPref() : String {

        return sharedPref.getString("Country", "ru").toString()
    }

    init {
//        getBreakingNews("ru")

            getBreakingPagingNews(getCountryPref())
    }


    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchForNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = repository.searchForNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{

        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
            return Resource.Error(response.message())

    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{

        if(response.isSuccessful){
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())

    }


    fun upsert(article: Article) = viewModelScope.launch {

        repository.upsert(article)
    }

    fun getAllArticles() = repository.getAllArticles()

    fun delete(article: Article) = viewModelScope.launch {

        repository.delete(article)
    }


//    fun getBreakingPagingNews(): LiveData<PagingData<Article>> {
//
//        return repository.getBreakingPagingNews().cachedIn(viewModelScope)
//    }

   lateinit var  breakingNewsPaging : Flow<PagingData<Article>>

    fun getBreakingPagingNews(countryCode: String) = viewModelScope.launch{

        breakingNewsPaging = repository.getBreakingPagingNews(countryCode).cachedIn(this)
    }



    val searchPagingNews : MutableLiveData<PagingData<Article>> = MutableLiveData()

    fun searchForNewsPaging(query : String) = viewModelScope.launch {

        searchPagingNews.postValue(repository.searchForNewsPaging(query).cachedIn(this).value)

    }


    fun hasInternetConnection () : Boolean {

            val connectivityManager = getApplication<NewsApplication>()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if(Build.VERSION.SDK_INT >= M) {

                val activeNetwork = connectivityManager.activeNetwork ?: return false

                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

                return when {

                    capabilities.hasTransport(TRANSPORT_WIFI) ->  true
                    capabilities.hasTransport(TRANSPORT_ETHERNET) ->  true
                    capabilities.hasTransport(TRANSPORT_CELLULAR) ->  true
                    else -> false
                }

            } else {

                connectivityManager.activeNetworkInfo?.run {
                    return when(type){
                        TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        return false
    }





}