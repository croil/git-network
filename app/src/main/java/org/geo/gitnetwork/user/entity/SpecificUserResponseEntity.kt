package org.geo.gitnetwork.user.entity

import org.geo.gitnetwork.model.User
import org.geo.gitnetwork.model.UserItem
import org.geo.gitnetwork.util.Constant
import java.io.File


//TODO: Moshi converter settings
data class SpecificUserResponseEntity(
    val id : Long,
    val login : String,
    val avatar_url : String,
    val name : String,
    val company : String,
    val email : String,
    val blog : String,
    val location : String,
    val bio : String,
    val followers : Int,
    val public_repos : Int
) {

    fun toUserItem(): UserItem = UserItem(
        name = name,
        company = company,
        email = email,
        blog = blog,
        location = location,
        bio = bio
    )
    fun toUser() : User = User(
        id = id,
        login = login,
        avatar = Constant.IMAGE_PATH + File.separatorChar + login + ".png",
        subs = followers,
        repos = public_repos
    )

}
