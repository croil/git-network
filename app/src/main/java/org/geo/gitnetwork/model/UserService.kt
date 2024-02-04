package org.geo.gitnetwork.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.geo.gitnetwork.retrofit.RetrofitClient
import org.geo.gitnetwork.retrofit.RetrofitUserSource
import java.util.concurrent.atomic.AtomicBoolean

typealias UserListener = (users: ArrayList<User>) -> Unit

class UserService : Service() {
    private val listeners = mutableSetOf<UserListener>()
    private var isRunning = AtomicBoolean(false)
    private var isLoading = AtomicBoolean(false)
    private var users = arrayListOf<User>()
    private lateinit var retrofit: RetrofitUserSource
    private lateinit var scope: CoroutineScope


    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(Dispatchers.IO)
        retrofit = RetrofitClient.source
    }


    fun listen(c: UserListener) {
        listeners.add(c)
        c.invoke(users)
    }

    fun unListen(c: UserListener) {
        listeners.remove(c)
    }


    fun loadUsers(amount: Int, callback: RefreshCallback) {
        if (isLoading.compareAndSet(false, true)) {
            scope.launch {
                request(amount, callback)
                isLoading.set(false)
            }
        }
    }

    private suspend fun request(amount: Int, callback: RefreshCallback) {
        val before = users.size
        val since = if (before != 0) users.last().id else 0
        try {
            users.addAll(retrofit.getUsers(since, amount))
            withContext(Dispatchers.Main) {
                callback.refresh(before, users.size - before)
            }
        } catch (e: Exception) {
            println(e.message) //TODO: Logging
        }


    }

    private suspend fun <T> listCopy(code: suspend () -> T): T {
        users = ArrayList(users)
        return code()
    }

    fun active(): AtomicBoolean = isRunning


    override fun onBind(intent: Intent?): SessionBinder = SessionBinder()


    inner class SessionBinder : Binder() {
        val service: UserService
            get() = this@UserService
    }

    interface RefreshCallback {
        fun refresh(from: Int, amount: Int)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        isRunning.set(false)

        // db close
    }


}