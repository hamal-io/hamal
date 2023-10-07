package io.hamal.lib.sdk.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val message: String?
)

class ApiException(message: String?) : RuntimeException(message) {
    constructor(err: ApiError) : this(err.message)
}