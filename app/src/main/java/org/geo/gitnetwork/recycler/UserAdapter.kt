package org.geo.gitnetwork.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.geo.gitnetwork.R
import org.geo.gitnetwork.databinding.ItemUserBinding
import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserListener

open class UserAdapter(
    protected open val userListener : (User) -> Unit,
    protected open val loadMoreUserCallback: () -> Unit
) : RecyclerView.Adapter<UserAdapter.ItemViewHolder>(), View.OnClickListener {


    var users: List<User> = emptyList()
        set(newList) {
            field = newList
            notifyDataSetChanged() //TODO: more efficient way(diffutil)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding).also {
            binding.root.setOnClickListener(this)
        }
    }

    override fun getItemCount(): Int = users.size


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        println(position)
        if (position == (0.8 * users.size).toInt()) { // if rest 20% -> load more users
            loadMoreUserCallback()
        }
        val user = users[position]
        with(holder.binding as ItemUserBinding) {
            holder.itemView.tag = user
            login.text = user.login
            subs.text = user.subs.toString()
            repos.text = user.repos.toString()
            avatar.setImageResource(R.drawable.ic_user_avatar)
        }
    }

    class ItemViewHolder(var binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View) {
        val user = v.tag as User
        userListener.invoke(user)
    }
}