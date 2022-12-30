package com.example.newsapp.models

import com.example.newsapp.db.entities.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)