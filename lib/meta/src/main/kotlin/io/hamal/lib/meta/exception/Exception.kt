package io.hamal.lib.meta.exception

open class HamalException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class IllegalArgumentException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class IllegalStateException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}


class InternalServerException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class NotFoundException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class NotImplementedYetException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}