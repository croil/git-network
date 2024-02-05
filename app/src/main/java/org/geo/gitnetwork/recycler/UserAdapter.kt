package org.geo.gitnetwork.recycler

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.geo.gitnetwork.R
import org.geo.gitnetwork.databinding.ItemUserBinding
import org.geo.gitnetwork.model.User
import java.io.File


class UserAdapter(
    private val root: File,
    private val userListener: (User) -> Unit,
    private val loadMoreUserCallback: () -> Unit
) : RecyclerView.Adapter<UserAdapter.ItemViewHolder>(), View.OnClickListener {

    private lateinit var holder: ItemViewHolder
    var users: List<User> = emptyList()
        set(newList) {
            val diffCallback = ItemDiffCallback(field, newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newList
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding).also {
            binding.root.setOnClickListener(this)
        }
    }

    override fun getItemCount(): Int = users.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        println(position)
        if (position > (0.8 * users.size).toInt()) { // if rest 20% -> load more users
            loadMoreUserCallback()
        }
        val user = users[position]
        with(holder.binding as ItemUserBinding) {
            holder.itemView.tag = user
            login.text = user.login
            subs.text = "${user.subs} подписчиков"
            repos.text = "${user.repos} репозиториев"
            val image = this@UserAdapter.root.resolve(user.avatar)
            if (image.exists()) {
                val bitmap = BitmapFactory.decodeFile(image.absolutePath)
                avatar.setImageBitmap(bitmap)
            } else {
                avatar.setImageResource(R.drawable.ic_user_avatar)
            }
        }
        this.holder = holder
    }

    class ItemViewHolder(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) = userListener.invoke(v.tag as User)

    fun position(): Int = holder.bindingAdapterPosition
}