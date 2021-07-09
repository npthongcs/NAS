package com.example.nsa

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nsa.adapter.NewsAdapter
import com.example.nsa.databinding.ActivityMainBinding
import com.example.nsa.listener.NewsOnClickListener
import com.example.nsa.model.Docs
import com.example.nsa.model.Filter
import com.example.nsa.network.CheckNetwork
import com.example.nsa.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), FilterDialog.FilterDialogListener, NewsOnClickListener {

    var pageCount = 1
    var isLoad = false
    var sort: String? = null
    var beginDate: String? = null
    var newsDesk: String? = null
    private var mQuery: String? = null
    private var listDocs = ArrayList<Docs>()
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: ActivityMainBinding
    lateinit var layoutManager: StaggeredGridLayoutManager
    private var viewModel: MainActivityViewModel = MainActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        makeApiCall()
        setupBinding()
        initScrollListener()
        newsAdapter.setOnCallBackListener(this)
    }

    private fun initScrollListener() {
        binding.rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = binding.rvNews.layoutManager?.childCount
                val totalItemCount = newsAdapter.itemCount
                var firstVisibleItems: IntArray? = null
                var pastVisibleItems = 0
                firstVisibleItems =
                    (binding.rvNews.layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(
                        firstVisibleItems
                    )
                if (firstVisibleItems != null && firstVisibleItems.isNotEmpty()) {
                    pastVisibleItems = firstVisibleItems[0]
                }
                if (!isLoad) {
                    if (visibleItemCount != null) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            isLoad = true
                            pageCount++
                            viewModel.fetchResponseWrapper(
                                mQuery,
                                beginDate,
                                sort,
                                newsDesk,
                                pageCount
                            )
                        }
                    }
                }
            }
        })
    }


    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()
        newsAdapter = NewsAdapter(listDocs)
        layoutManager = StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.rvNews.apply {
            setHasFixedSize(true)
            layoutManager = this@MainActivity.layoutManager
            adapter = newsAdapter
        }
    }

    private fun makeApiCall() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getResponseWrapperLiveDataObserver().observe(this, {
            if (it != null) {
                if (pageCount == 1) {
                    listDocs.clear()
                    listDocs.addAll(it.response.docs)
                    binding.rvNews.scrollToPosition(0)
                    newsAdapter.notifyDataSetChanged()
                } else {
                    val currentSize = newsAdapter.itemCount
                    listDocs.addAll(it.response.docs)
                    newsAdapter.notifyItemRangeInserted(currentSize, it.response.docs.size)
                    isLoad = false
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView: androidx.appcompat.widget.SearchView =
            searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    mQuery = query
                    pageCount = 1
                    if (CheckNetwork.isConnected(this@MainActivity)) {
                        viewModel.fetchResponseWrapper(mQuery, beginDate, sort, newsDesk, pageCount)
                    }
                } else {
                    pageCount = 1
                    mQuery = null
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                val dialog = FilterDialog()
                dialog.show(supportFragmentManager, "Filter dialog")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finishFilterDialog(filter: Filter) {
        beginDate = filter.beginDate
        sort = if (filter.sort == "None") null else filter.sort?.lowercase()

        if (filter.newsDesk.size > 0) {
            newsDesk = "news_desk:("
            for (i in filter.newsDesk) newsDesk += " \"$i\" "
            newsDesk += ")"
        } else newsDesk = null

        beginDate = beginDate?.replace("/", "")

        pageCount = 1
        if (CheckNetwork.isConnected(this@MainActivity)) {
            viewModel.fetchResponseWrapper(mQuery, beginDate, sort, newsDesk, pageCount)
        }
    }

    override fun onItemClick(data: Docs) {
        val builder = CustomTabsIntent.Builder()
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setShowTitle(true)

        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(data.web_url))
    }

}


