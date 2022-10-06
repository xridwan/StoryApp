package com.xridwan.mystory.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.xridwan.mystory.R
import com.xridwan.mystory.databinding.ActivityDetailBinding
import com.xridwan.mystory.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDetail()
    }

    private fun setDetail() {
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem

        binding.ivStory.load(story.photoUrl) {
            crossfade(true)
            crossfade(500)
            placeholder(android.R.color.darker_gray)
            error(R.drawable.ic_placeholder)
        }
        binding.tvStoryName.text = story.name
        binding.tvDescription.text = story.description
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}