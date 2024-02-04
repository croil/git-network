package org.geo.gitnetwork

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.geo.gitnetwork.databinding.ActivityUserBinding
import org.geo.gitnetwork.exception.UnspecifiedItemTypeException
import org.geo.gitnetwork.model.UserListener
import org.geo.gitnetwork.recycler.ComplexAdapter
import org.geo.gitnetwork.recycler.MarginItemDecorator

class UserActivity : BaseActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: ComplexAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityUserBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        viewDidLoad()
//        adapter = ComplexAdapter(
//            UserItem(
//                "{NAME}",
//                "{COMPANY}",
//                "{EMAIL}",
//                "{BLOG}",
//                "{LOCATION}",
//                "{BIO}"
//            )
//        ) { onUserPressed() }
//        setRecyclerViewOptions(binding.userRecyclerView)
        //userService.addListeners(userListener)
    }

    private fun setRecyclerViewOptions(recyclerView: RecyclerView) {
        recyclerView.adapter = this@UserActivity.adapter
        val layoutManager = GridLayoutManager(this, 2).also { setSpanSize(it) }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val itemPosition = layoutManager.findFirstVisibleItemPosition()
                binding.userName.text = if (itemPosition > ComplexAdapter.TOOLBAR) {
                    ComplexAdapter.SUBSCRIBERS
                } else {
                    "{USER}" // TODO: Username and set smooth
                }
            }
        })
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

    override fun onDestroy() {
        super.onDestroy()
       // userService.removeListener(userListener)
    }


    override fun onNavigateUp(): Boolean {
        finish()
        return true
    }

    private val userListener: UserListener = {
        adapter.users = it
    }
}