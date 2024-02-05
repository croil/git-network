package org.geo.gitnetwork

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.geo.gitnetwork.databinding.ActivityMainBinding
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.model.UserListener
import org.geo.gitnetwork.model.UserService
import org.geo.gitnetwork.recycler.MarginItemDecorator
import org.geo.gitnetwork.recycler.UserAdapter
import org.geo.gitnetwork.util.Constant


class MainActivity : BaseActivity() {
    private val userType = UserService.UserType.USER
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter

    private val usConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as UserService.SessionBinder).service
            userService = binder
            userService.listen(userListener, userType)
            if (userService.active(userType).compareAndSet(false, true)) {
                userService.loadUsers(15, refreshUsers, userType)
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
            dataDir.resolve(Constant.IMAGE_PATH),
            { onUserPressed(it) },
            { userService.loadUsers(20, refreshUsers, userType) }
        )
        setRecyclerViewOptions(binding.userRecyclerView, savedInstanceState)
        val serviceIntent = Intent(this, UserService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, usConnection, BIND_AUTO_CREATE)
//        userService.active(UserService.UserType.FOLLOWER).set(false)
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        state.putInt(Constant.POSITION, adapter.position())
    }

    private fun setRecyclerViewOptions(recyclerView: RecyclerView, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)
        recyclerView.adapter = this@MainActivity.adapter
        recyclerView.addItemDecoration(
            MarginItemDecorator(this@MainActivity, R.dimen.adaptive_item_margin)
        )
        recyclerView.setPercentagePadding(0.05, 0.0, 0.05, 0.0)
        if (savedInstanceState != null) {
            recyclerView.post {
                val position = savedInstanceState.getInt(Constant.POSITION)
                recyclerView.scrollToPosition(position)
            }
        }
    }

    private fun onUserPressed(user: User) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(Constant.LOGIN, user.login)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        userService.unListen(userListener)
    }


    private val userListener: UserListener = {
        adapter.users = it
    }

    val refreshUsers = object : UserService.RefreshCallback {
        override fun refresh(from: Int, amount: Int) {
            adapter.notifyItemRangeInserted(from, amount)
        }

        override fun refreshHeader(user: UserItem, subs: String) {
            TODO()
        }
    }
}


