package org.geo.gitnetwork.user

import org.geo.gitnetwork.user.entity.RawUserResponseEntity
import org.geo.gitnetwork.user.entity.SpecificUserResponseEntity
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("/users")
    suspend fun getUsers(
        @Query("since") since: Long,
        @Query("per_page") pages: Int
    ): List<RawUserResponseEntity>


    @GET("/users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ): SpecificUserResponseEntity
}