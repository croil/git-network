package org.geo.gitnetwork

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.geo.gitnetwork.databinding.ActivityMainBinding
import org.geo.gitnetwork.model.UserListener
import org.geo.gitnetwork.model.UserService
import org.geo.gitnetwork.recycler.MarginItemDecorator
import org.geo.gitnetwork.recycler.UserAdapter


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var userService: UserService
//    private val userService: _UserService
//        get() = (applicationContext as App).userService

    private val usConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as UserService.SessionBinder).service
            userService = binder
            userService.listen(userListener)
            if (userService.active().compareAndSet(false, true)) {
                userService.loadUsers(10, refreshUser)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewDidLoad()
        adapter = UserAdapter(
            { onUserPressed() },
            { userService.loadUsers(10, refreshUser) }
        )
        setRecyclerViewOptions(binding.userRecyclerView)
        val serviceIntent = Intent(this, UserService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, usConnection, BIND_AUTO_CREATE)
    }

    private fun setRecyclerViewOptions(recyclerView: RecyclerView) {
        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)
        recyclerView.adapter = this@MainActivity.adapter
        recyclerView.addItemDecoration(
            MarginItemDecorator(this@MainActivity, R.dimen.adaptive_item_margin)
        )
        recyclerView.setPercentagePadding(0.05, 0.0, 0.05, 0.0)
    }

    override fun onDestroy() {
        super.onDestroy()
        userService.unListen(userListener)
    }


    private val userListener: UserListener = { adapter.users = it }

    private val refreshUser: UserService.RefreshCallback = object : UserService.RefreshCallback {
        override fun refresh(from: Int, amount: Int) {
            adapter.notifyItemRangeInserted(from, amount)
        }
    }
}
