package org.geo.gitnetwork.user

import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem

interface UserSource {
    suspend fun getUser(login : String) : UserItem
    suspend fun getUsers(since : Long, perPage : Int = 30) : List<User>
}