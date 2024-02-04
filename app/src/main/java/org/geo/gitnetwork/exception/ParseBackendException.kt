package org.geo.gitnetwork.exception

class ParseBackendException(
    override val message: String,
    cause: Throwable
) : RuntimeException(message, cause) {
    constructor(cause: Throwable) : this("", cause)
}