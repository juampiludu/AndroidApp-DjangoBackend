package com.luduena.djangobackend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.luduena.djangobackend.api.ApiService
import com.luduena.djangobackend.api.User
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_btn.setOnClickListener {

            checkFields()
            register_btn.hideKeyboard()

        }

        go_to_login_btn.setOnClickListener {

            goToLoginButton()

        }

    }

    private fun goToLoginButton() {

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun checkFields() {

        when {

            reg_username.text.isEmpty() or reg_username.text.isNullOrBlank() -> {
                reg_username.error = "Complete this field"
            }
            reg_email.text.isEmpty() or reg_email.text.isNullOrBlank() -> {
                reg_email.error = "Complete this field"
            }
            !reg_email.text.isValidEmail() -> {
                reg_email.error = "Enter a valid email address"
            }
            reg_password.text.isEmpty() or reg_password.text.isNullOrBlank() -> {
                reg_password.error = "Complete this field"
            }
            reg_password.text.length < 8 -> {
                reg_password.error = "The password must have, at least, 8 characters"
            }
            else -> {
                register()
            }

        }

    }

    private fun register() {

        progress_bar_register.visibility = View.VISIBLE

        val username = reg_username.text.toString()
        val email = reg_email.text.toString()
        val password = reg_password.text.toString()

        val user = User(0, username, email, password, "")

        val retrofit = Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ApiService = retrofit.create(ApiService::class.java)
        val call = service.registerUser(user)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {

                if (response.isSuccessful) {

                    if (response.body() != null) {

                        val preferences: SharedPreferences = getSharedPreferences(
                            "myPrefs",
                            MODE_PRIVATE
                        )
                        val prefLoginEdit = preferences.edit()
                        prefLoginEdit.putBoolean("registration", true)
                        prefLoginEdit.apply()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                        Toast.makeText(this@RegisterActivity, "Successful registration!", Toast.LENGTH_SHORT).show()

                    }

                } else {
                    Toast.makeText(this@RegisterActivity, "Register failed. Try again later.", Toast.LENGTH_SHORT).show()
                    progress_bar_register.visibility = View.GONE
                    Log.d("fail", "fail")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Register failed. Try again later.", Toast.LENGTH_SHORT).show()
                progress_bar_register.visibility = View.GONE
                Log.d("fail", "fail")
            }

        })

    }

    private fun CharSequence.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}