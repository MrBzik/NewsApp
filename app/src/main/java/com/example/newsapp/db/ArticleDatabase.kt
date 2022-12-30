package com.example.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.db.entities.Article


@Database(entities = [Article::class],
    version = 2)

@TypeConverters(Converters::class)
abstract class ArticleDatabase() : RoomDatabase() {

    abstract fun getArticleDao() : ArticleDAO

    companion object{

        @Volatile
        private var database: ArticleDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = database ?: synchronized(LOCK){
            database ?: createDatabase(context).also { database = it }
        }


       private fun createDatabase(context: Context) = Room
            .databaseBuilder(context.applicationContext,
                ArticleDatabase::class.java, "article_db.db")
           .fallbackToDestructiveMigration()
            .build()
    }


}