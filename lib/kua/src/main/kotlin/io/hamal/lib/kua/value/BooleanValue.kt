package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

@Serializable
@SerialName("BooleanValue")
sealed class BooleanValue(
    val value: Boolean,
) : SerializableValue

@Serializable
@SerialName("TrueValue")
object TrueValue : BooleanValue(true) {
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseValue")
object FalseValue : BooleanValue(false) {
    override fun toString() = "false"
}