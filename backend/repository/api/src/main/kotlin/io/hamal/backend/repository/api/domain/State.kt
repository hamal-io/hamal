package io.hamal.backend.repository.api.domain

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.StatePayload
import kotlinx.serialization.Serializable

@Serializable
data class State(
    val correlation: Correlation,
    val payload: StatePayload
)