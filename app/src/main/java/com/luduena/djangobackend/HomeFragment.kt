package com.luduena.djangobackend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Post
import kotlinx.android.synthetic.main.post_list_fragment.*
import kotlinx.android.synthetic.main.post_list_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private lateinit var mRecyclerViewAdapter: RecyclerViewAdapter
    private lateinit var mPostList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.post_list_fragment, container, false)

        root.postsRecyclerView.setHasFixedSize(true)
        root.postsRecyclerView.layoutManager = LinearLayoutManager(this.context)

        mPostList = ArrayList()

        getAllPosts()

        root.swipe_refresh.setOnRefreshListener {

            mPostList.clear()
            getAllPosts()
            root.swipe_refresh.isRefreshing = false

        }

        return root
    }

    // Get all posts
    private fun getAllPosts() {

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        service.getAllPosts().enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {

                if (response.isSuccessful) {

                    val postList: List<Post>? = response.body()

                    if (postList != null) {
                        for (post in postList) {
                            val id = post.id
                            val author = post.author
                            val title = post.title
                            val publishedDate = post.published_date
                            mPostList.add(Post(id, author, title, "", publishedDate))
                        }

                        postsRecyclerView.visibility = View.VISIBLE
                        no_connection_view.visibility = View.GONE
                        post_list_progressBar.visibility = View.GONE

                        mRecyclerViewAdapter = RecyclerViewAdapter(mPostList)
                        postsRecyclerView.adapter = mRecyclerViewAdapter
                    }

                }

            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                t.printStackTrace()
                post_list_progressBar.visibility = View.GONE
                no_connection_view.visibility = View.VISIBLE
            }
        })

    }

}