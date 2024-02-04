package org.geo.gitnetwork.model

data class User(
    val id : Long,
    val login : String,
    val avatar : String,
    val subs : Int,
    val repos : Int
)
