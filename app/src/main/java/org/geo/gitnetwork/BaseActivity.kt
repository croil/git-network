package org.geo.gitnetwork

import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.model.UserListener
import org.geo.gitnetwork.model.UserService
import org.geo.gitnetwork.recycler.ComplexAdapter
import org.geo.gitnetwork.recycler.UserAdapter
import org.geo.gitnetwork.util.Constant

open class BaseActivity : AppCompatActivity() {

    protected lateinit var userService: UserService


    protected fun viewDidLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    protected fun RecyclerView.setPercentagePadding(
        start: Double, top: Double,
        end: Double, bottom: Double
    ) {
        check(start in 0.0..1.0)
        check(top in 0.0..1.0)
        check(end in 0.0..1.0)
        check(bottom in 0.0..1.0)
        val screenWidth = resources.displayMetrics.widthPixels
        val paddingStart = (screenWidth * start).toInt()
        val paddingTop = (screenWidth * top).toInt()
        val paddingEnd = (screenWidth * end).toInt()
        val paddingBottom = (screenWidth * bottom).toInt()
        this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }


//    protected val refreshUsers: UserService.RefreshCallback = object : UserService.RefreshCallback {
//        override fun refresh(from: Int, amount: Int) {
//            if (adapter is ComplexAdapter) {
//                (adapter as ComplexAdapter).notifyItemRangeInserted(from, amount)
//            }
//            else adapter.notifyItemRangeInserted(from, amount)
//        }
//
//        override fun refreshHeader(user: UserItem, subs: String) {
//            check(adapter is ComplexAdapter)
//            with(adapter as ComplexAdapter) {
//                this.user = user
//                this.notifyItemRangeInserted(0, 2)
////                this.subtitle = resources.getString(R.string.subs_title)
////                this.notifyItemRangeInserted(0, 2)
//            }
//        }
//    }
}