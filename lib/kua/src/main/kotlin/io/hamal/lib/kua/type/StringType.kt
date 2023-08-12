package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("StringType")
data class StringType(val value: String) : SerializableType {
    override fun toString(): String = value
}