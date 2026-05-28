package com.example.proyectoredseguridad.network

import com.example.proyectoredseguridad.network.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<Post>

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Response<Post>
}
