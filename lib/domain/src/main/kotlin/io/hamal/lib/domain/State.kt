package io.hamal.lib.domain

import kotlinx.serialization.Serializable

@Serializable
data class StatePayload(
    val contentType: String,
    val bytes: ByteArray
)