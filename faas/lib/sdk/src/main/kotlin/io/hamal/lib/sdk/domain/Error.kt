package io.hamal.lib.sdk.domain

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val message: String?
)