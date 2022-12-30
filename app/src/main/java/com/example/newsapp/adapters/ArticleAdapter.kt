package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.db.entities.Article

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var binding: ItemArticlePreviewBinding

        init {

            binding = ItemArticlePreviewBinding.bind(itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
      return ArticleViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article_preview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val currentArticle = differ.currentList[position]

        holder.binding.apply {

            tvSource.text = currentArticle.source?.name

            tvTitle.text = currentArticle.title

            tvDescription.text = currentArticle.description

            tvPublishedAt.text = currentArticle.publishedAt
        }

        Glide.with(holder.itemView).load(currentArticle.urlToImage).into(holder.binding.ivArticleImage)

        holder.itemView.setOnClickListener {

            onItemClickListener?.let {

                it(currentArticle)

            }

        }

    }

    override fun getItemCount(): Int {

       return differ.currentList.size
    }



   private var diffUtil = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {

            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {

            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this, diffUtil)


    private var onItemClickListener : ((Article) -> Unit)? = null

    fun setOnItemCLickListener (listener : (Article) -> Unit) {

        onItemClickListener = listener

    }


}