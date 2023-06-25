package io.hamal.lib.domain

import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import kotlinx.serialization.Serializable

@Serializable
data class State(
    val contentType: ContentType,
    val content: Content
)

@Serializable
data class CorrelatedState(
    val correlation: Correlation,
    val state: State
)