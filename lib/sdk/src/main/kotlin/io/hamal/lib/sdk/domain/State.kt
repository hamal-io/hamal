package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("do not have separate dto")
data class ApiGetStateResponse(
    val correlation: Correlation,
    val contentType: String
)


@Serializable
@Deprecated("do not have separate dto")
data class ApiSetStateResponse(
    val correlation: Correlation,
    val contentType: String
)
