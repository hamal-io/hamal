package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ErrorValue")
data class ErrorValue(val message: String) : Value {
    override val type = Value.Type.Error
}