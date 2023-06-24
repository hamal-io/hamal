package io.hamal.lib.domain

import kotlinx.serialization.Serializable

@Serializable
data class HamalError(
    val message: String?
)