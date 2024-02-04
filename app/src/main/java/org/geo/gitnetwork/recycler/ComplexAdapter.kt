package org.geo.gitnetwork.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import org.geo.gitnetwork.databinding.ItemUserBinding
import org.geo.gitnetwork.databinding.SubToolbarBinding
import org.geo.gitnetwork.databinding.UserBinding
import org.geo.gitnetwork.exception.UnspecifiedItemTypeException
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.model.UserListener

class ComplexAdapter(
    private val userItem: UserItem,
    override val actionListener : UserListener,
    override val loadMoreUserCallback: () -> Unit
) : UserAdapter(actionListener, loadMoreUserCallback) {


    companion object {
        const val USER: Int = 0
        const val TOOLBAR: Int = 1
        const val ITEM: Int = 2
        const val SUBSCRIBERS: String = "Подписчики" // TODO: remove
        const val USERS: String = "Пользователи" // TODO: remove
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            USER -> ItemViewHolder(UserBinding.inflate(inflater, parent, false))
            TOOLBAR -> ItemViewHolder(SubToolbarBinding.inflate(inflater, parent, false))
            ITEM -> ItemViewHolder(ItemUserBinding.inflate(inflater, parent, false))
                .also { holder ->
                    holder.binding.root.setOnClickListener(this)
                }

            else -> throw UnspecifiedItemTypeException(
                "View type is not user, sub toolbar or regular item"
            )
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (holder.itemViewType) {
            USER -> with(holder.binding as UserBinding) {
                name.text = userItem.name
                company.text = userItem.company
                email.text = userItem.email
                blog.text = userItem.blog
                location.text = userItem.location
                bio.text = userItem.bio
            }

            TOOLBAR -> (holder.binding as SubToolbarBinding).subsTitle.text = SUBSCRIBERS
            ITEM -> super.onBindViewHolder(holder, position - 2)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> USER
            1 -> TOOLBAR
            else -> ITEM
        }
    }
}