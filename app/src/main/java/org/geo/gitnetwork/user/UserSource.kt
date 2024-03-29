package org.geo.gitnetwork.user

import okhttp3.ResponseBody
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem

interface UserSource {
    suspend fun getUser(login : String) : UserItem
    suspend fun getUserAvatar(id : Long) : ResponseBody
    suspend fun getUsers(since : Long, perPage : Int = 30) : List<User>
    suspend fun getFollowers(login: String, page : Int, perPage: Int = 30) : List<User>
}