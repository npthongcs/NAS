package com.example.nsa

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nsa.adapter.NewsAdapter
import com.example.nsa.databinding.ActivityMainBinding
import com.example.nsa.listener.NewsOnClickListener
import com.example.nsa.listener.OnLoadMoreListener
import com.example.nsa.model.Docs
import com.example.nsa.model.Filter
import com.example.nsa.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), FilterDialog.FilterDialogListener, NewsOnClickListener {

    private var mQuery: String? = null
    var sort: String? = null
    var beginDate: String? = null
    var newsDesk: String? = null
    var pageCount = 1
    var isLoad = false
    var listDocs = ArrayList<Docs>()
    var isQuery: Boolean = false
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: ActivityMainBinding
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

    private fun initScrollListener(){
        binding.rvNews.addOnScrollListener(object : RecyclerViewLoadMoreScroll(binding.rvNews.layoutManager as StaggeredGridLayoutManager){
            override fun onLoadMore() {
                if (!isLoad){
                    isLoad = true
                    Log.d("page count",pageCount.toString())
                    pageCount++
                    isQuery = false
                    viewModel.fetchResponseWrapper(mQuery,beginDate,sort,newsDesk,pageCount)
                }
            }
        })
    }


    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.executePendingBindings()
        newsAdapter = NewsAdapter(listDocs)

        binding.rvNews.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
            (layoutManager as StaggeredGridLayoutManager).gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE;
            adapter = newsAdapter
        }
    }

    private fun makeApiCall() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getResponseWrapperLiveDataObserver().observe(this,{
            if (it!=null){
                Log.d("response wrapper",it.toString())
                if (pageCount == 1) listDocs.clear()
                listDocs.addAll(it.response.docs)
                newsAdapter.notifyDataSetChanged()
                isLoad = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView: androidx.appcompat.widget.SearchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    Log.d("search",query)
                    Log.d("begin date", beginDate.toString())
                    Log.d("news desk",newsDesk.toString())
                    Log.d("sort",sort.toString())
                    mQuery = query
                    pageCount = 1
                    viewModel.fetchResponseWrapper(mQuery,beginDate,sort,newsDesk,pageCount)
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
        when (item.itemId){
            R.id.action_filter -> {
                val dialog = FilterDialog()
                dialog.show(supportFragmentManager,"Filter dialog")
                Log.d("filter icon","is clicked")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finishFilterDialog(filter: Filter) {
        Log.d("data when click save",filter.toString())
        sort = if (filter.sort=="None") null else filter.sort?.lowercase()
        beginDate = filter.beginDate
        if (filter.newsDesk.size>0){
            newsDesk = "news_desk:("
            for (i in filter.newsDesk) newsDesk+=" \"$i\" "
            newsDesk+=")"
        } else newsDesk = null

        beginDate = beginDate?.replace("/","")

        Log.d("begin date", beginDate.toString())
        Log.d("news desk",newsDesk.toString())
        Log.d("sort",sort.toString())
        pageCount = 1
        viewModel.fetchResponseWrapper(mQuery,beginDate,sort,newsDesk,pageCount)
    }

    override fun onItemClick(data: Docs) {
        Log.d("headline click",data.headline.main)
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this,R.color.design_default_color_primary))
        builder.setShareState(CustomTabsIntent.SHARE_STATE_DEFAULT)

    }

}

