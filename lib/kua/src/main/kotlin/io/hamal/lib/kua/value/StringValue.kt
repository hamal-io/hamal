package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("StringValue")
data class StringValue(val value: String) : SerializableValue {
    override fun toString(): String = value
}