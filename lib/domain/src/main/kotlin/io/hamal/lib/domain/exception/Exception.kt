package io.hamal.lib.domain.exception

sealed class BaseException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}


class IllegalArgumentException(message: String) : BaseException(message)
