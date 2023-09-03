package io.hamal.lib.sdk.hub.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val message: String?
)