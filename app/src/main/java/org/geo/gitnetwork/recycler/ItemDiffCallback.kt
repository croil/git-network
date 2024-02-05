package org.geo.gitnetwork.recycler

import androidx.recyclerview.widget.DiffUtil
import org.geo.gitnetwork.model.User


class ItemDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMessage = oldList[oldItemPosition]
        val newMessage = newList[newItemPosition]
        return oldMessage == newMessage
    }

}
