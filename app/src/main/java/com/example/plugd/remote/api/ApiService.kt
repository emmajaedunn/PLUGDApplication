package com.example.plugd.remote.api

import com.example.plugd.data.LoginRequest
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.dto.ActivityDto
import com.example.plugd.remote.api.dto.CreateEventDto
import com.example.plugd.remote.api.dto.TokenResponse
import com.example.plugd.remote.api.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // -----------------
    // Authentication
    // -----------------
    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<TokenResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    // Firebase login (optional)
    @POST("api/auth/firebase")
    suspend fun loginWithFirebase(@Body body: Map<String, String>): TokenResponse

    // -----------------
    // User Profile
    // -----------------
    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") userId: String): Response<UserDto>

    @POST("api/profile/{id}/update")
    suspend fun updateProfileField(
        @Path("id") userId: String,
        @Query("field") field: String,
        @Query("value") value: String
    ): Response<Unit>

    // -----------------
    // Events
    // -----------------
    @GET("events")
    suspend fun listEvents(): Response<List<EventEntity>>

    @POST("events")
    suspend fun addEvent(@Body event: CreateEventDto): Response<EventEntity>


    @PUT("events/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: EventEntity): Response<Unit>

    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Unit>

    /*
    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: EventEntity): Response<Unit>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Unit>
*/
    // -----------------
    // Activities
    // -----------------
    @GET("api/activities/{userId}/feed")
    suspend fun getActivityFeed(@Path("userId") userId: String): List<ActivityDto>

    @POST("api/activities/{userId}/add")
    suspend fun addActivity(
        @Path("userId") userId: String,
        @Body activity: ActivityDto
    )
}




















/*package com.example.plugd.remote.api

import com.example.plugd.data.LoginRequest
import com.example.plugd.remote.api.dto.ActivityDto
import com.example.plugd.remote.api.dto.TokenResponse
import com.example.plugd.remote.api.dto.UserDto
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.dto.CreateEventDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // -----------------
    // Authentication
    // -----------------
    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<TokenResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    // Firebase login (optional)
    @POST("api/auth/firebase")
    suspend fun loginWithFirebase(@Body body: Map<String, String>): TokenResponse

    // -----------------
    // User Profile
    // -----------------
    @GET("api/profile/{id}")
    suspend fun getProfile(@Path("id") userId: String): Response<UserDto>

    @POST("api/profile/{id}/update")
    suspend fun updateProfileField(
        @Path("id") userId: String,
        @Query("field") field: String,
        @Query("value") value: String
    ): Response<Unit>

    // -----------------
    // Events
    // -----------------
    @GET("events")
    suspend fun listEvents(): Response<List<EventEntity>>

    @POST("events")
    suspend fun addEvent(@Body event: CreateEventDto): Response<EventEntity>
    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: String, @Body event: EventEntity): Response<Unit>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Unit>

    // -----------------
    // Activities
    // -----------------
    @GET("api/activities/{userId}/feed")
    suspend fun getActivityFeed(@Path("userId") userId: String): List<ActivityDto>

    @POST("api/activities/{userId}/add")
    suspend fun addActivity(
        @Path("userId") userId: String,
        @Body activity: ActivityDto
    )
}*/











/*package com.example.plugd.remote.api

import com.example.plugd.data.LoginRequest
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.dto.ActivityDto
import com.example.plugd.remote.api.dto.TokenResponse
import com.example.plugd.remote.api.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("api/events/{userId}")
    suspend fun getUserEvents(@Path("userId") userId: String): List<EventEntity>
    @POST("events")
    suspend fun addEvent(@Body event: EventEntity)

    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Int, @Body event: EventEntity)

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int)

    /*Events
    @GET("api/events")
    suspend fun getEvents(): List<EventEntity>
    @POST("events")
    suspend fun addEvent(@Body event: EventEntity)
*/



    // Auth endpoints
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<TokenResponse>

    @POST("api/auth/firebase")
    suspend fun loginWithFirebase(@Body body: Map<String, String>): TokenResponse

    // Profile endpoints
    @GET("api/profile/{id}")
    suspend fun getProfile(@retrofit2.http.Path("id") userId: String): Response<UserDto>

    @POST("api/profile/{id}/update")
    suspend fun updateProfileField(
        @retrofit2.http.Path("id") userId: String,
        @retrofit2.http.Query("field") field: String,
        @retrofit2.http.Query("value") value: String
    ): Response<Unit>

    // Activity endpoints
    @GET("api/activities/{userId}/feed")
    suspend fun getActivityFeed(
        @Path("userId") userId: String
    ): List<ActivityDto>

    @POST("api/activities/{userId}/add")
    suspend fun addActivity(
        @Path("userId") userId: String,
        @Body activity: ActivityDto
    )
}*/























/*package com.example.plugd.remote.api

import com.example.plugd.data.localRoom.entity.EventEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import com.example.plugd.remote.api.dto.ActivityDto

data class LoginRequest(val email: String, val password: String)
data class UserDto(val id: Int, val username: String, val name: String, val email: String, val role: String?)
data class TokenResponse(val token: String, val user: UserDto)
data class EventDto(val id: Int, val title: String, val description: String?)

interface ApiService {
    @GET("api/events")
    suspend fun getEvents(): List<EventEntity>

    @POST("events")
    suspend fun addEvent(@Body event: EventEntity)

    // Auth endpoints
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<TokenResponse>

    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<TokenResponse>

    @POST("api/auth/firebase")
    suspend fun loginWithFirebase(@Body body: Map<String, String>): TokenResponse

    // Profile endpoints
    @GET("api/profile/{id}")
    suspend fun getProfile(@retrofit2.http.Path("id") userId: String): Response<UserDto>

    @POST("api/profile/{id}/update")
    suspend fun updateProfileField(
        @retrofit2.http.Path("id") userId: String,
        @retrofit2.http.Query("field") field: String,
        @retrofit2.http.Query("value") value: String
    ): Response<Unit>

    // Activity endpoints
    @GET("api/activity/{userId}/feed")
    suspend fun getActivityFeed(
        @retrofit2.http.Path("userId") userId: String
    ): List<ActivityDto>

    @POST("api/activity/{userId}/add")
    suspend fun addActivity(
        @retrofit2.http.Path("userId") userId: String,
        @Body activity: ActivityDto
    )
}*/