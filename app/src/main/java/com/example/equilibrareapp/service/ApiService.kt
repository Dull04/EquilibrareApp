package com.example.equilibrareapp.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part


data class LoginResult(
    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("displayName")
    val displayName: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("idToken")
    val idToken: String,

    @field:SerializedName("refreshToken")
    val refreshToken: String
)

data class UserResult(
    @field:SerializedName("uid")
    val uid: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("displayName")
    val displayName: String,

    @field:SerializedName("photoURL")
    val photoURL: String
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class RegisterResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: UserResult
)

data class LoginEmailResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LoginGoogleResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LogoutResponse(
    @field:SerializedName("message")
    val message: String
)

data class LogoutRequest(
    val uid: String
)

data class ProfileResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: UserResult
)

data class PredictResponse(
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("prediction")
    val prediction: Double,
    @field:SerializedName("label")
    val label: String
)

data class PredictRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)

data class GetDataResponse(
    @SerializedName("content")
    val content: String,
    @SerializedName("title")
    val title: String
)

data class UpdateProfileResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserResult
)

interface ApiService {
    @Multipart
    @POST("/auth/signup")
    fun register(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("displayName") displayName: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<RegisterResponse>

    @POST("/auth/login")
    fun loginEmail(
        @Body loginRequest: LoginRequest
    ): Call<LoginEmailResponse>

    @POST("/saveData")
    fun predict(
        @Header("Authorization") bearerToken: String,
        @Body predict: PredictRequest
    ): Call<PredictResponse>

    @POST("/auth/logout")
    fun logout(
        @Body request: LogoutRequest
    ): Call<LogoutResponse>

    @GET("/profile")
    fun getProfile(
        @Header("Authorization") bearerToken: String
    ): Call<ProfileResponse>

    @GET("/getData")
    fun getData(
    @Header("Authorization") bearerToken: String
    ):Call<GetDataResponse>
    @Multipart
    @POST("/profile/update")
    fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("displayName") displayName: RequestBody,
        @Part photo: MultipartBody.Part
    ):Call<UpdateProfileResponse>
}
