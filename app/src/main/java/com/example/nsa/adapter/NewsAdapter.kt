package com.example.nsa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nsa.R
import com.example.nsa.databinding.OnlyTextItemBinding
import com.example.nsa.databinding.StandardItemBinding
import com.example.nsa.listener.NewsOnClickListener
import com.example.nsa.model.Docs
import com.example.nsa.model.Headline
import com.example.nsa.model.StandardItem

class NewsAdapter(var mDocs: ArrayList<Docs>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var newsOnClickListener: NewsOnClickListener? = null

    fun setOnCallBackListener(newsOnClickListener: NewsOnClickListener){
        this.newsOnClickListener = newsOnClickListener
    }

    inner class OnlyTextViewHolder(private val binding: OnlyTextItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Headline){
            binding.headline = data
        }
    }

    inner class StandardViewHolder(private val binding: StandardItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: StandardItem){
            binding.standard = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType==1) StandardViewHolder(StandardItemBinding.inflate(inflater)).apply {
            itemView.setOnClickListener {
                this@NewsAdapter.newsOnClickListener?.onItemClick(mDocs[bindingAdapterPosition])
            }
        }
        else OnlyTextViewHolder(OnlyTextItemBinding.inflate(inflater)).apply {
            itemView.setOnClickListener {
                this@NewsAdapter.newsOnClickListener?.onItemClick(mDocs[bindingAdapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
            var url = ""
            val multimedia = mDocs[position].multimedia
            for (i in multimedia){
                if (i.subtype == "thumbnail"){
                    url = i.url
                    break
                }
            }
            (holder as StandardViewHolder).bind(StandardItem(mDocs[position].headline.main,url))
        } else (holder as OnlyTextViewHolder).bind(mDocs[position].headline)
    }

    override fun getItemCount(): Int {
        return mDocs.size
    }

    override fun getItemViewType(position: Int): Int {
        val multimedia = mDocs[position].multimedia
        multimedia.forEach {
            if (it.subtype == "thumbnail") return 1 // 1 --> standard
        }
        return -1
    }

    companion object{
        private const val baseURL = "https://www.nytimes.com/"
        @JvmStatic
        @BindingAdapter("loadThumbnail")
        fun loadThumbnail(thumbnailID : ImageView,url: String?){
            Glide.with(thumbnailID.context)
                .load("$baseURL$url")
                .override(300,300)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_background)
                .into(thumbnailID)
        }
    }

}