package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.Correlation
import kotlinx.serialization.Serializable

@Serializable
data class State(
    val correlation: Correlation,
    val contentType: String,
    val bytes: ByteArray
)