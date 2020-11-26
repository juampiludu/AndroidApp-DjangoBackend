package com.luduena.djangobackend

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Comment
import com.luduena.djangobackend.api.Post
import kotlinx.android.synthetic.main.activity_post_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PostDetail : AppCompatActivity() {

    private var authorName: String? = ""
    private var postId: Int = 0

    private lateinit var mRecyclerViewAdapter: CommentsRecyclerViewAdapter
    private lateinit var mCommentList: ArrayList<Comment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        scrollView2.visibility = View.GONE

        supportActionBar?.title = resources.getString(R.string.post_detail_title)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        comments_recyclerView.setHasFixedSize(true)
        comments_recyclerView.layoutManager = LinearLayoutManager(this)

        mCommentList = ArrayList()

        val intent = intent
        postId = intent.getIntExtra("postId", 0)
        authorName = intent.getStringExtra("authorName")
        getPost(postId)
        getComments(postId)

        if (comment_content_text.text.isEmpty() or comment_content_text.text.isNullOrBlank()) {
            post_comment_button.isClickable = false
            post_comment_button.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
            post_comment_button.visibility = View.GONE
        }

        comment_content_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                when {
                    comment_content_text.text.isEmpty() or comment_content_text.text.isNullOrBlank() -> {
                        post_comment_button.isClickable = false
                        post_comment_button.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
                        post_comment_button.visibility = View.GONE
                    }
                    else -> {
                        post_comment_button.isClickable = true
                        post_comment_button.setColorFilter(Color.parseColor("#44b78b"), PorterDuff.Mode.SRC_IN)
                        post_comment_button.visibility = View.VISIBLE
                        post_comment_button.setOnClickListener {

                            comment_content_text.error = null
                            postComment(comment_content_text.text.toString())
                            post_comment_button.hideKeyboard()

                        }
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }


    // get post detail
    private fun getPost(postId: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call: Call<Post> = service.getPost(postId)

        call.enqueue(object : Callback<Post> {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Post>, response: Response<Post>) {

                if (response.isSuccessful) {

                    val post: Post? = response.body()

                    if (post != null) {

                        val title = post.title
                        val body = post.body_text
                        val author = post.author
                        val publishedDate = post.published_date

                        postTitle.text = title
                        postBody.text = body
                        postBody.justificationMode = JUSTIFICATION_MODE_INTER_WORD
                        postAuthor.text = "by $author • $publishedDate"

                        scrollView2.visibility = View.VISIBLE
                        progress_bar_post_detail.visibility = View.GONE

                    }

                } else {

                    Log.d("fail", "fail")
                    Toast.makeText(
                        this@PostDetail,
                        "It was an error retrieving data. Try again later.",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {

                Log.d("fail", "fail")
                Toast.makeText(
                    this@PostDetail,
                    "It was an error retrieving data. Try again later.",
                    Toast.LENGTH_LONG
                ).show()

            }

        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val preferences: SharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val username = preferences.getString("username", "")
        return if (authorName == username) {
            menuInflater.inflate(R.menu.delete_post_btn, menu)
            true
        }
        else {
            super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.delete_post -> {

                MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure to delete this post? It will be deleted permanently.")
                    .setPositiveButton("Delete") { _, _ ->
                        deletePost(postId)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .show()

                true

            }

            else -> super.onOptionsItemSelected(item)

        }
    }

    // delete post
    private fun deletePost(id: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authToken = SharedDataGetSet.getMySavedToken(this)

        val service = retrofit.create(ApiService::class.java)
        val call: Call<List<Post>> = service.deletePost(authToken, id)

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Toast.makeText(this@PostDetail, "Post deleted successfully!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(
                    this@PostDetail,
                    "Error deleting post. Try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    // get comments
    private fun getComments(postId: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getPostComments(postId)

        call.enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {

                if (response.isSuccessful) {

                    val commentList: List<Comment>? = response.body()

                    if (commentList != null) {

                        var commentCounter = 0

                        for (comment in commentList) {

                            commentCounter++
                            val id = comment.id
                            val author = comment.author
                            val content = comment.content
                            val publishedDate = comment.published_date

                            mCommentList.add(Comment(id, postId, author, content, publishedDate))

                        }

                        mRecyclerViewAdapter = CommentsRecyclerViewAdapter(
                            this@PostDetail,
                            mCommentList
                        )
                        comments_recyclerView.adapter = mRecyclerViewAdapter
                        comments_info.text = "$commentCounter comments"

                    }

                } else {
                    Toast.makeText(this@PostDetail, "Comments couldn't load", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Toast.makeText(this@PostDetail, "Comments couldn't load", Toast.LENGTH_SHORT).show()
            }
        })

    }

    // post comment
    private fun postComment(
        content: String
    ) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authToken = SharedDataGetSet.getMySavedToken(this)

        val service = retrofit.create(ApiService::class.java)

        val comment: Comment? = Comment(0, postId, "", content, "")

        val call = service.addComment(authToken, comment!!)

        call.enqueue(object : Callback<Comment> {

            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {

                Log.d("good", "good")
                Toast.makeText(
                    this@PostDetail,
                    "Published!",
                    Toast.LENGTH_LONG
                ).show()

                comment_content_text.text.clear()
                mCommentList.clear()
                getComments(postId)

            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Log.d("fail", "fail")
                Toast.makeText(
                    this@PostDetail,
                    "It was an error publishing your comment. Try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}