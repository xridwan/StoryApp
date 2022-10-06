package com.xridwan.mystory.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.xridwan.mystory.R
import com.xridwan.mystory.databinding.ItemStoryBinding
import com.xridwan.mystory.response.ListStoryItem

class StoryAdapter(
    private val stories: ArrayList<ListStoryItem>,
    private val listener: Listener
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.ivStory.load(item.photoUrl) {
                crossfade(true)
                crossfade(500)
                placeholder(android.R.color.darker_gray)
                error(R.drawable.ic_placeholder)
            }
            binding.tvStoryName.text = item.name

            itemView.setOnClickListener {
                listener.onListener(item)
            }
        }
    }

    interface Listener {
        fun onListener(data: ListStoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

}