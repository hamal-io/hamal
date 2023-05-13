package io.hamal.lib.domain.vo

import kotlinx.serialization.Serializable

@Serializable
sealed interface Code {
    val value: String
}

@Serializable
data class HamalScriptCode(
    override val value: String
) : Code