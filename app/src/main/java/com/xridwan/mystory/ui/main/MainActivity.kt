package com.xridwan.mystory.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xridwan.mystory.R
import com.xridwan.mystory.ViewModelFactory
import com.xridwan.mystory.databinding.ActivityMainBinding
import com.xridwan.mystory.datastore.UserPreferences
import com.xridwan.mystory.network.ApiConfig
import com.xridwan.mystory.response.ListStoryItem
import com.xridwan.mystory.response.StoryResponse
import com.xridwan.mystory.ui.add.AddActivity
import com.xridwan.mystory.ui.authentication.LoginActivity
import com.xridwan.mystory.ui.detail.DetailActivity
import com.xridwan.mystory.ui.utils.hide
import com.xridwan.mystory.ui.utils.show
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity(), StoryAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                startActivity(Intent(this, AddActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                dialogLogout()
                true
            }
            else -> true
        }
    }

    private fun dialogLogout() {
        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Exit this app?")
            setCancelable(false)
            setPositiveButton("Ok") { _, _ ->
                viewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.getToken().observe(this) { userToken ->
            if (userToken.isNotEmpty()) {
                getStory(userToken)
            }
        }
    }

    private fun getStory(token: String) {
        val userToken = "Bearer $token"

        binding.progressCircular.show()

        val client = ApiConfig.getApiService().getStories(userToken)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setRecyclerView(responseBody.listStory)
                    }
                }
                binding.progressCircular.hide()
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.d(this@MainActivity.toString(), "onError : ${t.message}")
                binding.progressCircular.hide()
            }
        })
    }

    private fun setRecyclerView(list: ArrayList<ListStoryItem>) {
        storyAdapter = StoryAdapter(list, this)
        val llm = LinearLayoutManager(this)
        binding.rvStories.layoutManager = llm
        binding.rvStories.setHasFixedSize(true)
        binding.rvStories.adapter = storyAdapter
    }

    override fun onListener(data: ListStoryItem) {
        startActivity(
            Intent(this, DetailActivity::class.java)
                .putExtra(DetailActivity.EXTRA_DATA, data)
        )
    }
}