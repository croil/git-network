package org.geo.gitnetwork.model

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.geo.gitnetwork.retrofit.RetrofitClient
import org.geo.gitnetwork.retrofit.RetrofitUserSource
import org.geo.gitnetwork.util.Constant
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

typealias UserListener = (users: ArrayList<User>) -> Unit

class UserService : Service() {
    private val listeners = mutableSetOf<UserListener>()
    private lateinit var root: File
    private var isRunning = AtomicBoolean(false)
    private var isLoading = AtomicBoolean(false)
    private var users = arrayListOf<User>()
    private lateinit var retrofit: RetrofitUserSource
    private lateinit var scope: CoroutineScope


    override fun onCreate() {
        super.onCreate()
        root = this.dataDir
        scope = CoroutineScope(Dispatchers.IO)
        retrofit = RetrofitClient.client
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
            val newUsers = retrofit.getUsers(since, amount)
            for (user in newUsers) {
                loadImage(root.resolve(user.avatar), user.id)
            }
            users.addAll(newUsers)
            withContext(Dispatchers.Main) {
                callback.refresh(before, users.size - before)
            }
        } catch (e: Exception) {
            println(e.message) //TODO: Logging
        }


    }

    private suspend fun loadImage(file: File, id: Long) {
        delay(Constant.REQUEST_DELAY)
        val body = retrofit.getUserAvatar(id)
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