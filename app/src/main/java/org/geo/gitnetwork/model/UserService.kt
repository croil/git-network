package org.geo.gitnetwork.model

import android.app.Service
import android.content.Intent
import android.os.Binder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.geo.gitnetwork.R
import org.geo.gitnetwork.retrofit.RetrofitClient
import org.geo.gitnetwork.retrofit.RetrofitUserSource
import org.geo.gitnetwork.util.Constant
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean

typealias UserListener = (users: ArrayList<User>) -> Unit

class UserService : Service() {
    private val listeners = mutableSetOf<UserListener>()
    private val userState = ServiceState()
    private val followerState = ServiceState()
    private var mainUser: UserItem = UserItem.EMPTY
    private var mainUserLogin: String = ""
    private var users = arrayListOf<User>()
    private var followers = arrayListOf<User>()
    private lateinit var root: File
    private lateinit var retrofit: RetrofitUserSource
    private lateinit var scope: CoroutineScope


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mainUserLogin = intent?.getStringExtra(Constant.LOGIN) ?: ""
        return START_NOT_STICKY

    }

    override fun onCreate() {
        super.onCreate()
        root = this.dataDir.resolve(Constant.IMAGE_PATH)
        scope = CoroutineScope(Dispatchers.IO)
        retrofit = RetrofitClient.client
    }


    fun listen(c: UserListener, userType: UserType) {
        listeners.add(c)
        when (userType) {
            UserType.FOLLOWER -> c.invoke(followers)
            UserType.USER -> c.invoke(users)
        }
    }

    fun unListen(c: UserListener) {
        listeners.remove(c)
    }


    fun loadHead(callback: RefreshCallback) {
        scope.launch {
            try {
                val user = retrofit.getUser(mainUserLogin)
                withContext(Dispatchers.Main) {
                    callback.refreshHeader(user, resources.getString(R.string.subs_title))
                }
            } catch (e: Exception) { //todo: logging
            }
        }
    }


    fun loadUsers(amount: Int, callback: RefreshCallback, userType: UserType) {
        val state = when (userType) {
            UserType.USER -> userState
            UserType.FOLLOWER -> followerState
        }
        if (state.loading().compareAndSet(false, true)) {
            scope.launch {
                request(amount, callback, userType)
                state.loading().set(false)
            }
        }
    }

    private suspend fun request(amount: Int, callback: RefreshCallback, userType: UserType) {
        var loaded = 0
        var page = 1
        while (loaded < amount) {
            try {
                val container = when (userType) {
                    UserType.USER -> users
                    UserType.FOLLOWER -> followers
                }
                val before = container.size
                val since = if (before != 0) container.last().id else 0
                val newUsers = when (userType) {
                    UserType.USER -> retrofit.getUsers(since, minOf(amount, 5))
                    UserType.FOLLOWER -> retrofit.getFollowers(
                        mainUserLogin,
                        page,
                        minOf(amount, 5)
                    )
                }
                if (newUsers.isEmpty()) {
                    break
                }
                for (user in newUsers) {
                    loadImage(root.resolve(user.avatar), user.id)
                }
                container.addAll(newUsers)
                println("SZ: " + container.size)
                withContext(Dispatchers.Main) {
                    val from = when(userType) {
                        UserType.USER -> before
                        UserType.FOLLOWER -> before + 2
                    }
                    callback.refresh(from, container.size - before)
                }
                page++
                loaded += minOf(amount, 5)
            } catch (e: Exception) {
                throw e//TODO: Logging
            }
        }
    }

    enum class UserType {
        USER, FOLLOWER
    }

    private suspend fun loadImage(file: File, id: Long) {
        delay(Constant.REQUEST_DELAY)
        val body = retrofit.getUserAvatar(id)
        if (!root.exists()) {
            root.mkdir()
        }
        if (!file.exists()) file.createNewFile()
        body.byteStream().use {
            val fos = FileOutputStream(file)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (it.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }

    override fun onBind(intent: Intent?): SessionBinder = SessionBinder()


    inner class SessionBinder : Binder() {
        val service: UserService
            get() = this@UserService
    }

    interface RefreshCallback {
        fun refresh(from: Int, amount: Int)
        fun refreshHeader(user: UserItem, subs: String)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        userState.active().set(false)
        followerState.active().set(false)
        // db close
    }

    fun active(userType: UserType): AtomicBoolean {
        return when (userType) {
            UserType.USER -> userState
            UserType.FOLLOWER -> followerState
        }.active()
    }

    fun clearFollowers() {
        followers.clear()
    }

}