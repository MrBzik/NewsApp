package com.example.newsapp.ui.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.utils.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class BreakingNewsFragment() : BaseFragment<FragmentBreakingNewsBinding> (
            FragmentBreakingNewsBinding::inflate
        ) {

//    lateinit var newsAdapter : ArticleAdapter



    lateinit var newsAdapter : PagingNewsAdapter

    private val TAG = "BreakingNewsFragment"



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecycleView()

        setUpCountryMenu()

        newsAdapter.setOnItemCLickListener {

            val bundle = Bundle().apply {

                putSerializable("article", it)


            }
            
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)

        }

        newsAdapter.addLoadStateListener {

            if (it.refresh is LoadState.Loading ||
                    it.append is LoadState.Loading)
                showProgressBar()
            else {
                hideProgressBar()
            }

        }





        updateAdapter()

//        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
//            when(it){
//               is Resource.Success -> {
//
//                   hideProgressBar()
//
//                   it.data?.let { newsResponse ->
//                       newsAdapter.differ.submitList(newsResponse.articles)
//                   }
//               }
//                is Resource.Error -> {
//
//                    hideProgressBar()
//
//                    it.message?.let { message ->
//
//                        Log.e(TAG, message)
//                    }
//                }
//                 is Resource.Loading -> {
//
//                     showProgressBar()
//                 }
//            }
//        })

//        lifecycleScope.launch {
//
//            viewModel.getBreakingPagingNews().observe(viewLifecycleOwner){
//                it?.let {
//                    newsAdapter.submitData(lifecycle, it)
//                }
//
//            }
//        }






    }

    private fun hideProgressBar(){
        bind.paginationProgressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar(){
        bind.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecycleView(){

//        newsAdapter = ArticleAdapter()
//
        newsAdapter = PagingNewsAdapter()

        bind.rvBreakingNews.apply {

            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

        }

    }

        fun setUpCountryMenu(){

            (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

                   menuInflater.inflate(R.menu.breaking_country_menu, menu)


                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    var preference = ""
                    when(menuItem.itemId) {
                        R.id.miItal -> preference = "it"
                        R.id.miRus -> preference = "ru"
                        R.id.miUkr -> preference = "ua"
                        R.id.miUSA -> preference = "us"

                    }


                    viewModel.editor.apply{
                        putString("Country", preference)
                        apply()

                    }
                    viewModel.getBreakingPagingNews(preference)
                    updateAdapter()

                    return true
                }


            }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        }

    fun updateAdapter() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                if(viewModel.hasInternetConnection()){

                    viewModel.breakingNewsPaging.collectLatest {

                        newsAdapter.submitData(it)
                    }
                }

                else {

                    Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show()

                }
            }

        }

    }

}