package com.luduena.djangobackend

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Post
import kotlinx.android.synthetic.main.post_list_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyPostsList: AppCompatActivity() {

    private lateinit var mRecyclerViewAdapter: RecyclerViewAdapter
    private lateinit var mPostList: ArrayList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_list_fragment)

        supportActionBar?.title = "My Posts"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        postsRecyclerView.setHasFixedSize(true)
        postsRecyclerView.layoutManager = LinearLayoutManager(this)

        mPostList = ArrayList()

        getUserPosts()

        swipe_refresh.setOnRefreshListener {

            mPostList.clear()
            getUserPosts()
            swipe_refresh.isRefreshing = false

        }

    }

    private fun getUserPosts() {

        val preferences: SharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val id = preferences.getInt("userId", 0)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call: Call<List<Post>> = service.getUserPosts(id)

        call.enqueue(object: Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {

                if (response.isSuccessful) {

                    val postList: List<Post>? = response.body()

                    if (postList != null) {

                        for (post in postList) {

                            val postId = post.id
                            val author = post.author
                            val title = post.title
                            val publishedDate = post.published_date
                            mPostList.add(Post(postId, author, title, "", publishedDate))

                        }

                        postsRecyclerView.visibility = View.VISIBLE
                        no_connection_view.visibility = View.GONE
                        post_list_progressBar.visibility = View.GONE

                        mRecyclerViewAdapter = RecyclerViewAdapter(mPostList)
                        postsRecyclerView.adapter = mRecyclerViewAdapter
                    }

                }
                else {
                    Toast.makeText(this@MyPostsList, "Error retrieving data. Try again later.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                post_list_progressBar.visibility = View.GONE
                no_connection_view.visibility = View.VISIBLE
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}