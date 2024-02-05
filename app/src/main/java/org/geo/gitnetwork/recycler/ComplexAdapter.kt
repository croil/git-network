package org.geo.gitnetwork.recycler

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import okhttp3.internal.notifyAll
import org.geo.gitnetwork.R
import org.geo.gitnetwork.databinding.ItemUserBinding
import org.geo.gitnetwork.databinding.SubToolbarBinding
import org.geo.gitnetwork.databinding.UserBinding
import org.geo.gitnetwork.exception.UnspecifiedItemTypeException
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import java.io.File

class ComplexAdapter(
    private val root: File,
    private val actionListener: (User) -> Unit,
    val loadMoreUserCallback: () -> Unit
) : RecyclerView.Adapter<ComplexAdapter.ItemViewHolder>(), View.OnClickListener {

    var items: List<User> = arrayListOf()
        set(value) {
            val diffCallback = ItemDiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    var user: UserItem = UserItem.EMPTY
        set(value) {
            field = value
            notifyDataSetChanged() //TODO: fix
        }

    private val subtitle: String = "Подписчики"

    companion object {
        const val USER: Int = 0
        const val TOOLBAR: Int = 1
        const val ITEM: Int = 2
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        println(position)
        when (holder.itemViewType) {
            USER -> with(holder.binding as UserBinding) {
                name.text = user.name
                company.text = user.company
                email.text = user.email
                blog.text = user.blog
                location.text = user.location
                bio.text = user.bio
                val image = this@ComplexAdapter.root.resolve(user.avatar)
                if (image.exists()) {
                    println(image.absolutePath)
                    val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                    avatar.setImageBitmap(bitmap)
                } else {
                    avatar.setImageResource(R.drawable.ic_user_avatar)
                }
            }

            TOOLBAR -> with(holder.binding as SubToolbarBinding) {
                subsTitle.text = this@ComplexAdapter.subtitle
            }

            ITEM -> {
                if (position > (0.8 * (items.size + 2)).toInt()) { // if rest 20% -> load more users
                    loadMoreUserCallback()
                }
                val user = items[position - 2]
                with(holder.binding as ItemUserBinding) {
                    holder.itemView.tag = user
                    login.text = user.login
                    subs.text = "${user.subs} подписчиков"
                    repos.text = "${user.repos} репозиториев"
                    val image = this@ComplexAdapter.root.resolve(user.avatar)
                    if (image.exists()) {
                        val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                        avatar.setImageBitmap(bitmap)
                    } else {
                        avatar.setImageResource(R.drawable.ic_user_avatar)
                    }
                }
            }
        }
    }

    class ItemViewHolder(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> USER
            1 -> TOOLBAR
            else -> ITEM
        }
    }

    override fun getItemCount() = items.size + 2

    override fun onClick(v: View) = actionListener.invoke(v.tag as User)
}