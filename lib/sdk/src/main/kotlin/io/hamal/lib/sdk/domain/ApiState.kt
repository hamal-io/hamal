package io.hamal.lib.sdk.domain

import io.hamal.lib.domain.Correlation
import kotlinx.serialization.Serializable

@Serializable
data class ApiGetStateResponse(
    val correlation: Correlation,
    val contentType: String
)


@Serializable
data class ApiSetStateResponse(
    val correlation: Correlation,
    val contentType: String
)
