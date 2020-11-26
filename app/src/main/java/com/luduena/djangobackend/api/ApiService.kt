package com.luduena.djangobackend.api

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("account/api-token-auth/")
    fun login(@Body login: Login): Call<User>

    @POST("account/register/")
    fun registerUser(@Body user: User): Call<User>

    @POST("post/add/")
    fun addPost(@Header("Authorization") authToken: String, @Body post: Post?): Call<Post>

    @POST("post/comment/add/")
    fun addComment(@Header("Authorization") authToken: String, @Body comment: Comment): Call<Comment>

    @GET("post/list/")
    fun getAllPosts(): Call<List<Post>>

    @GET("post/list/post/{id}/")
    fun getPost(@Path(value = "id", encoded = true) id: Int): Call<Post>

    @GET("post/comment/list/{id}/")
    fun getPostComments(@Path(value = "id", encoded = true) id: Int): Call<List<Comment>>

    @GET("account/profile/{id}/")
    fun getUserProfile(@Path(value = "id", encoded = true) id: Int): Call<User>

    @GET("post/list/author/{id}/")
    fun getUserPosts(@Path(value = "id", encoded = true) id: Int): Call<List<Post>>

    @DELETE("post/delete/{id}/")
    fun deletePost(@Header("Authorization") authToken: String, @Path(value = "id", encoded = true) id: Int): Call<List<Post>>

    @DELETE("post/comment/delete/{id}/")
    fun deleteComment(@Header("Authorization") authToken: String, @Path(value = "id", encoded = true) id: Int): Call<List<Comment>>

    companion object {

        const val BASE_URL: String = "https://theblogapp.pythonanywhere.com/api/"

    }

}