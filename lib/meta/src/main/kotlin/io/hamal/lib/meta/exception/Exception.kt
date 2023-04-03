package io.hamal.lib.meta.exception

sealed class HamalException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}


class IllegalArgumentException(message: String) : HamalException(message)

class InternalServerException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class NotFoundException(message: String) : HamalException(message)
