package com.example.plugd.remote.api

import com.example.plugd.data.LoginRequest
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.dto.ActivityDto
import com.example.plugd.remote.api.dto.CreateEventDto
import com.example.plugd.remote.api.dto.TokenResponse
import com.example.plugd.remote.api.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication
    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<TokenResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @POST("api/auth/firebase")
    suspend fun loginWithFirebase(@Body body: Map<String, String>): TokenResponse

    // User Profile
    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") userId: String): Response<UserDto>

    @POST("api/profile/{id}/update")
    suspend fun updateProfileField(
        @Path("id") userId: String,
        @Query("field") field: String,
        @Query("value") value: String
    ): Response<Unit>

    // Events
    @GET("api/events")
    suspend fun listEvents(): Response<List<EventEntity>>

    @POST("api/events")
    suspend fun addEvent(@Body event: CreateEventDto): Response<EventEntity>

    // Activity
    @GET("api/activities/{userId}/feed")
    suspend fun getActivityFeed(@Path("userId") userId: String): List<ActivityDto>

    @POST("api/activities/{userId}/add")
    suspend fun addActivity(
        @Path("userId") userId: String,
        @Body activity: ActivityDto
    )
}
