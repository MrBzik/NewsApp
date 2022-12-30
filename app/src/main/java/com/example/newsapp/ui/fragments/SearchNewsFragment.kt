package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.ArticleAdapter
import com.example.newsapp.adapters.PagingNewsAdapter
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchNewsFragment() : BaseFragment<FragmentSearchNewsBinding>
    (FragmentSearchNewsBinding::inflate) {

    lateinit var newsAdapter : ArticleAdapter


//    lateinit var newsAdapter : PagingNewsAdapter


    private val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpRecycleView()



        newsAdapter.setOnItemCLickListener {

            val bundle = Bundle().apply {

                putSerializable("article", it)

            }
            findNavController().navigate(R.id.action_searchNews_to_articleFragment, bundle)
        }


//        newsAdapter.addLoadStateListener {
//
//            if (it.refresh is LoadState.Loading ||
//                it.append is LoadState.Loading)
//                showProgressBar()
//            else {
//                hideProgressBar()
//            }
//        }




        var job: Job? = null

        bind.etSearch.addTextChangedListener { etView ->

            job?.cancel()

            job = MainScope().launch {
                delay(800L)

                if(etView.toString().isNotEmpty()) {

                    etView?.let {

                        viewModel.searchForNews(it.toString())
                    }

                }

            }
        }





            viewModel.searchNews.observe(viewLifecycleOwner, Observer {

                when(it){
                   is Resource.Success -> {

                       hideProgressBar()

                       it.data?.let { newsResponse ->

                           newsAdapter.differ.submitList(newsResponse.articles)
                       }
                    }

                    is Resource.Error -> {

                        hideProgressBar()

                        it.message?.let { message ->

                            Log.e(TAG, message)
                        }
                    }

                    is Resource.Loading -> {

                        showProgressBar()

                    }

                }

            })



    }
    private fun hideProgressBar(){
        bind.paginationProgressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        bind.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecycleView(){

        newsAdapter = ArticleAdapter()

//        newsAdapter = PagingNewsAdapter()

        bind.rvSearchNews.apply {

            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }

    }


}