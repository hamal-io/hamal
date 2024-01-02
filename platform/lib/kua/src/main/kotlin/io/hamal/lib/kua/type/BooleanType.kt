package io.hamal.lib.kua.type

fun booleanOf(value: Boolean) = if (value) True else False

sealed class BooleanType(
    val value: Boolean,
) : SerializableType()

object True : BooleanType(true) {
    override fun toString() = "true"
}

object False : BooleanType(false) {
    override fun toString() = "false"
}