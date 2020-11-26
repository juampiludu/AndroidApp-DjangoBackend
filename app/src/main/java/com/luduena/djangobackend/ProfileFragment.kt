package com.luduena.djangobackend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Post
import com.luduena.djangobackend.api.User
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.profile_fragment.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var mainHandler: Handler

    private val updateTextTask = object: Runnable {
        override fun run() {
            countUserPosts()
            mainHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.profile_fragment, container, false)

        val prefs: SharedPreferences = root.context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val id: Int = prefs.getInt("userId", 0)
        val username = prefs.getString("username", "")
        val email = prefs.getString("email", "")

        val logoutBtn: MaterialButton = root.findViewById(R.id.logout)

        if (username == "" && email == "") {
            getUserProfile(id)
        }

        root.prof_user.text = username
        root.prof_email.text = email

        logoutBtn.setOnClickListener {

            logout()

        }

        root.my_posts_btn.setOnClickListener {

            val intent = Intent(context, MyPostsList::class.java)
            startActivity(intent)

        }

        mainHandler = Handler(Looper.getMainLooper())

        return root
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
    }

    private fun getUserProfile(id: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call: Call<User> = service.getUserProfile(id)

        call.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {

                        val username = body.username
                        val email = body.email

                        val preferences: SharedPreferences = context!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                        val editPrefs: SharedPreferences.Editor = preferences.edit()
                        editPrefs.putString("username", username)
                        editPrefs.putString("email", email)
                        editPrefs.apply()

                        prof_user.text = username
                        prof_email.text = email

                    }

                }
                else {
                    Toast.makeText(context, "It was an error retrieving data, try again later.", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "It was an error retrieving data, try again later.", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun countUserPosts() {

        val preferences: SharedPreferences = context!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val id = preferences.getInt("userId", 0)

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call: Call<List<Post>> = service.getUserPosts(id)

        call.enqueue(object: Callback<List<Post>> {

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {

                if (response.isSuccessful) {

                    val postList: List<Post>? = response.body()
                    var counter = 0

                    if (postList != null) {

                        for (post in postList) {

                            counter++
                            my_posts_btn.text = "My Posts ($counter)"

                        }
                    }

                }

            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {

            }
        })

    }

    private fun logout() {

        MaterialAlertDialogBuilder(context!!, R.style.AlertDialogTheme)
            .setTitle("Logout")
            .setMessage("Are you sure to logout?")
            .setNegativeButton("Cancel") {_,_->

            }
            .setPositiveButton("Logout") {_,_->
                val preferences: SharedPreferences = context!!.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
                val prefsEdit: SharedPreferences.Editor = preferences.edit()
                prefsEdit.putBoolean("loggedin", false)
                prefsEdit.putString("token", "")
                prefsEdit.putString("username", "")
                prefsEdit.putString("email", "")
                prefsEdit.putInt("userId", 0)
                prefsEdit.apply()

                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
            .show()


    }

}