package com.example.newsapp.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.db.entities.Article


class BreakingNewsDataSource (private val countryCode : String) : PagingSource<Int, Article>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val position = params.key ?: 1
            val response = RetrofitInstance.api.getBreakingNews(pageNumber =  position, countryCode = countryCode)

            LoadResult.Page(data = response.body()!!.articles, prevKey =
            if (position == 1) null
            else position - 1,
                nextKey = position+1)

        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }



}