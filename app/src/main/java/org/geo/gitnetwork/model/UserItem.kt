package org.geo.gitnetwork.model

data class UserItem(
    val avatar: String,
    val name: String,
    val company: String,
    val email: String,
    val blog: String,
    val location: String,
    val bio: String
) {

    companion object {
        val EMPTY = UserItem("", "", "", "", "", "", "")
    }

    override fun toString(): String {
        return "UserItem(avatar='$avatar', name='$name', company='$company', email='$email', blog='$blog', location='$location', bio='$bio')"
    }
}
