package org.geo.gitnetwork.retrofit

import kotlinx.coroutines.delay
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.user.UserApi
import org.geo.gitnetwork.user.UserSource


class RetrofitUserSource(
    config: RetrofitConfig
) : RetrofitSource(config), UserSource {

    private val userApi = retrofit.create(UserApi::class.java)

    override suspend fun getUsers(since: Long, perPage: Int): List<User> = handleRetrofitException {
        return@handleRetrofitException userApi.getUsers(since, perPage).mapNotNull {
            try {
                delay(100)
                userApi.getUser(it.login).toUser()
            } catch (e: Exception) { // todo: logging
                null
            }
        }
    }

    override suspend fun getUser(login: String): UserItem = handleRetrofitException {
        return@handleRetrofitException userApi.getUser(login).toUserItem()
    }
}