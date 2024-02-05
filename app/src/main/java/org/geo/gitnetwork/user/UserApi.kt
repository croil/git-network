package org.geo.gitnetwork.user

import okhttp3.ResponseBody
import org.geo.gitnetwork.user.entity.RawUserResponseEntity
import org.geo.gitnetwork.user.entity.SpecificUserResponseEntity
import org.geo.gitnetwork.util.Constant
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface UserApi {

    @GET("/users") // no base url because avatar url
    suspend fun getUsers(
        @Query("since") since: Long,
        @Query("per_page") pages: Int
    ): List<RawUserResponseEntity>

    @GET("/users/{login}/followers")
    suspend fun getFollowers(
        @Path("login") login: String,
        @Query("page") page : Int,
        @Query("per_page") perPage : Int
    ) : List<RawUserResponseEntity>

    @GET("/users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): SpecificUserResponseEntity


    @Streaming
    @GET("${Constant.IMAGE_URL}/u/{id}?v=4")
    suspend fun getUserAvatar(@Path("id") id : Long) : Response<ResponseBody>

}