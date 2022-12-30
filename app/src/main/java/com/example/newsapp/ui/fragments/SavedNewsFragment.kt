package com.example.newsapp.ui.fragments


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.ArticleAdapter
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.google.android.material.snackbar.Snackbar


class SavedNewsFragment() : BaseFragment<FragmentSavedNewsBinding>
    (FragmentSavedNewsBinding::inflate) {

    lateinit var newsAdapter : ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()

        newsAdapter.setOnItemCLickListener {

            val bundle = Bundle().apply {

                putSerializable("article", it)

            }

            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)

        }

        val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.delete(article).also {
                    Snackbar.make(view, "Article deleted", Snackbar.LENGTH_LONG).apply {

                       setAction("UNDO") {
                           viewModel.upsert(article)
                       }
                    }.show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelper).apply {
            attachToRecyclerView(bind.rvSavedNews)
        }



        viewModel.getAllArticles().observe(viewLifecycleOwner, Observer {

            newsAdapter.differ.submitList(it)

        })


    }

    fun setUpRecycleView () {

        newsAdapter = ArticleAdapter()

        bind.rvSavedNews.apply {

            adapter = newsAdapter

            layoutManager = LinearLayoutManager(activity)

        }



    }



}