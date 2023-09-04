package io.hamal.lib.sdk.hub

import kotlinx.serialization.Serializable

@Serializable
data class HubError(
    val message: String?
)

class HubException(message: String?) : RuntimeException(message) {
    constructor(err: HubError) : this(err.message)
}