package org.geo.gitnetwork.exception

class UnspecifiedItemTypeException(
    override val message: String,
    cause: Throwable?
) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
    constructor(cause: Throwable) : this("", cause)
}