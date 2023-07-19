package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

@Serializable
@SerialName("BooleanValue")
sealed class BooleanValue(
    val boolean: Boolean,
) : Value

@Serializable
@SerialName("TrueValue")
object TrueValue : BooleanValue(true) {
    override val type = Value.Type.Boolean
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseValue")
object FalseValue : BooleanValue(false) {
    override val type = Value.Type.Boolean
    override fun toString() = "false"
}