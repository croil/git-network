package org.geo.gitnetwork.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecorator(
    context: Context,
    marginDp: Int
) : RecyclerView.ItemDecoration() {

    private val margin: Int = context.resources.getDimensionPixelSize(marginDp)

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = margin
        outRect.top = margin
        outRect.right = margin
        outRect.bottom = margin
    }

}