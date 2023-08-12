package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

@Serializable
@SerialName("BooleanType")
sealed class BooleanType(
    val value: Boolean,
) : SerializableType

@Serializable
@SerialName("TrueType")
object TrueValue : BooleanType(true) {
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseType")
object FalseValue : BooleanType(false) {
    override fun toString() = "false"
}