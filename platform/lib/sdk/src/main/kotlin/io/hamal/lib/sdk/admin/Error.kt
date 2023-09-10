package io.hamal.lib.sdk.admin

import kotlinx.serialization.Serializable

@Serializable
data class AdminError(
    val message: String?
)

class AdminException(message: String?) : RuntimeException(message) {
    constructor(err: AdminError) : this(err.message)
}