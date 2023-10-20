package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ErrorType")
data class ErrorType(val value: String) : SerializableType()