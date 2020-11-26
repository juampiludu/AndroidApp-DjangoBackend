package com.luduena.djangobackend

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Post
import kotlinx.android.synthetic.main.activity_publish_post.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class PublishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_post)

        supportActionBar?.title = resources.getString(R.string.publish_post_title)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        titleText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.length > 150) {
                    titleText.error = "The limit of characters are 150"
                } else {
                    titleText.error = null
                }

            }
        })

        add_post_btn.setOnClickListener {

            when {
                titleText.text.isEmpty() or titleText.text.isNullOrBlank() -> {
                    titleText.error = "Complete this field"
                }
                titleText.text.length > 150 -> {
                    titleText.error = "The limit of characters are 150"
                }
                bodyText.text.isEmpty() or bodyText.text.isNullOrBlank() -> {
                    bodyText.error = "Complete this field"
                }
                else -> {

                    addPost(
                        titleText.text.toString(),
                        bodyText.text.toString(),
                        Calendar.getInstance().time.toString()
                    )

                    add_post_btn.hideKeyboard()

                }

            }

        }
    }

    // Add Post
    private fun addPost(
        postTitle: String,
        postBodyText: String,
        postPublishedDate: String
    ) {

        progress_bar_publish.visibility = View.VISIBLE

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val queryToken = SharedDataGetSet.getMySavedToken(this)

        val service = retrofit.create(ApiService::class.java)

        val post: Post? = Post(
            0,
            "",
            postTitle,
            postBodyText,
            postPublishedDate
        )

        val call: Call<Post> = service.addPost(queryToken, post)

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                Log.d("good", "good")
                Toast.makeText(
                    this@PublishActivity,
                    "Your post has been published successfully!",
                    Toast.LENGTH_LONG
                ).show()

                onBackPressed()
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.d("fail", "fail")
                Toast.makeText(
                    this@PublishActivity,
                    "It was an error publishing your post. Try again later.",
                    Toast.LENGTH_LONG
                ).show()
                progress_bar_publish.visibility = View.GONE
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}
