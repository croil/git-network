package org.geo.gitnetwork

import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

open class BaseActivity : AppCompatActivity() {

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

    protected fun onUserPressed() {
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
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

}