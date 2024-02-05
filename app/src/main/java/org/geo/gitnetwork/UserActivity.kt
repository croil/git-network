package org.geo.gitnetwork

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.geo.gitnetwork.databinding.ActivityUserBinding
import org.geo.gitnetwork.exception.UnspecifiedItemTypeException
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.model.UserListener
import org.geo.gitnetwork.model.UserService
import org.geo.gitnetwork.recycler.ComplexAdapter
import org.geo.gitnetwork.recycler.MarginItemDecorator
import org.geo.gitnetwork.util.Constant

class UserActivity : BaseActivity() {
    private val userType = UserService.UserType.FOLLOWER
    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: ComplexAdapter

    private val userConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = (service as UserService.SessionBinder).service
            userService = binder
            userService.listen(itemListener, userType)
            userService.loadHead(refreshUsers)
            if (userService.active(userType).compareAndSet(false, true)) {
                userService.loadUsers(5, refreshUsers, userType)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    val refreshUsers = object  : UserService.RefreshCallback {
        override fun refresh(from: Int, amount: Int) {
            adapter.notifyItemRangeInserted(from, amount)
        }

        override fun refreshHeader(user: UserItem, subs: String) {
            adapter.user = user
            adapter.notifyItemRangeInserted(0,2)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewDidLoad()
        adapter = ComplexAdapter(
            dataDir.resolve(Constant.IMAGE_PATH),
            { onUserPressed(it) },
            { userService.loadUsers(20, refreshUsers, userType) }
        )
        println(resources.getString(R.string.subs_title))
        val login = intent.getStringExtra(Constant.LOGIN) ?: ""
        println(("EXTRA: " + intent.getStringExtra(Constant.LOGIN)))
        setRecyclerViewOptions(binding.userRecyclerView, savedInstanceState, login)
        val serviceIntent = Intent(this, UserService::class.java).apply {
            putExtra(Constant.LOGIN, login)
        }
        startService(serviceIntent)
        bindService(serviceIntent, userConnection, BIND_AUTO_CREATE)
    }

    private fun onUserPressed(user: User) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(Constant.LOGIN, user.login)
        removeRecyclerView()
        userService.active(userType).set(false)
        startActivity(intent)
    }

    private fun removeRecyclerView() {
        val size = adapter.itemCount
        adapter.items = arrayListOf()
        userService.clearFollowers()
        adapter.notifyItemRangeRemoved(0, size)
    }

    private fun setRecyclerViewOptions(recyclerView: RecyclerView, state: Bundle?, login: String) {
        recyclerView.adapter = this@UserActivity.adapter
        val layoutManager = GridLayoutManager(this, 2).also { setSpanSize(it) }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val itemPosition = layoutManager.findFirstVisibleItemPosition()
                binding.userName.text = if (itemPosition <= ComplexAdapter.TOOLBAR) {
                    login
                } else { //TODO: make it smoothly
                    resources.getString(R.string.subs_title)
                }

            }
        })
        if (state != null) {
            recyclerView.post {
                val position = state.getInt(Constant.POSITION)
                recyclerView.scrollToPosition(position)
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(MarginItemDecorator(this, R.dimen.adaptive_item_margin))
        recyclerView.setPercentagePadding(0.05, 0.0, 0.05, 0.0)
    }

    private fun setSpanSize(gridLayoutManager: GridLayoutManager) {
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (this@UserActivity.adapter.getItemViewType(position)) {
                    ComplexAdapter.USER, ComplexAdapter.TOOLBAR -> 2
                    ComplexAdapter.ITEM -> 1
                    else -> throw UnspecifiedItemTypeException("")
                }
            }
        }
    }


    private val itemListener : UserListener = {
        adapter.items = it
    }

    override fun onDestroy() {
        super.onDestroy()
        userService.unListen(itemListener)
    }

    override fun onNavigateUp(): Boolean {
        removeRecyclerView()
        userService.active(userType).set(false)
        finish()
        return true
    }
}

