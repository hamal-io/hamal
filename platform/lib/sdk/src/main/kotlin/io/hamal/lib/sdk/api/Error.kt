package io.hamal.lib.sdk.api

data class ApiError(
    val message: String?
)

class ApiException(message: String?) : RuntimeException(message) {
    constructor(err: ApiError) : this(err.message)
}