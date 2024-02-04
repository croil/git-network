package org.geo.gitnetwork.exception

class BackendException(override val message : String, val code : Int) : RuntimeException(message)