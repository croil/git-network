package org.geo.gitnetwork.retrofit

import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.user.UserApi
import org.geo.gitnetwork.user.UserSource
import org.geo.gitnetwork.util.Constant


class RetrofitUserSource(
    config: RetrofitConfig
) : RetrofitSource(config), UserSource {

    private val userApi = retrofit.create(UserApi::class.java)


    override suspend fun getUsers(since: Long, perPage: Int): List<User> = handleRetrofitException {
        return@handleRetrofitException userApi.getUsers(since, perPage).mapNotNull {
            try {
                delay(Constant.REQUEST_DELAY)
                userApi.getUser(it.login).toUser()
            } catch (e: Exception) { // todo: logging
                null
            }
        }
    }

    override suspend fun getFollowers(login : String, page: Int, perPage: Int): List<User> =
        handleRetrofitException {
            return@handleRetrofitException userApi.getFollowers(login, page, perPage).mapNotNull {
                try {
                    delay(Constant.REQUEST_DELAY)
                    userApi.getUser(it.login).toUser()
                } catch (e: Exception) { // todo: logging
                    null
                }
            }
        }

    override suspend fun getUserAvatar(id: Long): ResponseBody = handleRetrofitException {
        return@handleRetrofitException userApi.getUserAvatar(id).body()!!
    }

    override suspend fun getUser(login: String): UserItem = handleRetrofitException {
        return@handleRetrofitException userApi.getUser(login).toUserItem()
    }

}