package com.luduena.djangobackend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.Login
import com.luduena.djangobackend.api.User
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (alreadyLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_btn.setOnClickListener {

            loginButton()
            login_btn.hideKeyboard()

        }

        go_to_reg_btn.setOnClickListener {

            goToRegisterButton()

        }

    }

    private fun goToRegisterButton() {

        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun loginButton() {

        emptyFields()

    }

    private fun login() {

        progress_bar_login.visibility = View.VISIBLE

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val user = username.text.toString()
        val pass = password.text.toString()

        val login = Login(user, pass)
        val call: Call<User> = service.login(login)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {

                    if (response.body() != null) {

                        val body = response.body()!!

                        val token: String = body.token
                        val id: Int = body.id

                        val preferences: SharedPreferences = getSharedPreferences(
                            "myPrefs",
                            Context.MODE_PRIVATE
                        )
                        val prefLoginEdit: SharedPreferences.Editor = preferences.edit()
                        prefLoginEdit.putBoolean("loggedin", true)
                        prefLoginEdit.putString("token", token)
                        prefLoginEdit.putInt("userId", id)
                        prefLoginEdit.apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@LoginActivity, "It was an error logging in, try again later.", Toast.LENGTH_SHORT).show()
                        progress_bar_login.visibility = View.GONE
                    }
                }
                else {
                    Toast.makeText(this@LoginActivity, "It was an error logging in. Check if you are passing right information.", Toast.LENGTH_SHORT).show()
                    progress_bar_login.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "It was an error logging in, try again later.", Toast.LENGTH_SHORT).show()
                progress_bar_login.visibility = View.GONE
            }

        })

    }

    private fun emptyFields() {

        when {

            username.text.isEmpty() or username.text.isNullOrBlank() -> {
                username.error = "This field is required"
            }

            password.text.isEmpty() or password.text.isNullOrBlank() -> {
                password.error = "This field is required"
            }

            else -> {
                username.error = null
                password.error = null
                login()
            }

        }

    }

    private fun alreadyLoggedIn(): Boolean {
        val preferences: SharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return preferences.getBoolean("loggedin", false)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}