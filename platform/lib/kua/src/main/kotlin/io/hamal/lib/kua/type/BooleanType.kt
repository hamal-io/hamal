package io.hamal.lib.kua.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun booleanOf(value: Boolean) = if (value) True else False

@Serializable
@SerialName("BooleanType")
sealed class BooleanType(
    val value: Boolean,
) : SerializableType()

@Serializable
@SerialName("TrueType")
object True : BooleanType(true) {
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseType")
object False : BooleanType(false) {
    override fun toString() = "false"
}