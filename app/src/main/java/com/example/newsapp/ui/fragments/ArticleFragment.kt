package com.example.newsapp.ui.fragments


import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.newsapp.databinding.FragmentArticleBinding
import com.google.android.material.snackbar.Snackbar

class ArticleFragment() : BaseFragment<FragmentArticleBinding>(
            FragmentArticleBinding::inflate
) {

    val args : ArticleFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = args.article

        bind.webView.apply {

            webViewClient = WebViewClient()
            article.url?.let {
                loadUrl(it)
            }


        }

        bind.fab.setOnClickListener {

            viewModel.upsert(article)
            Snackbar.make(view, "Article added", Snackbar.LENGTH_SHORT).show()

        }



    }


}