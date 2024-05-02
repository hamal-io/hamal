package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeBoolean : TypePrimitive() {
    override val identifier = TypeIdentifier("Boolean")
}

fun booleanValueOf(value: Boolean) = if (value) ValueTrue else ValueFalse

sealed class ValueBoolean(
    private val value: Boolean
) : Value {

    override val type get() = TypeBoolean

    companion object {
        fun of(value: Boolean) = if (value) ValueTrue else ValueFalse
    }

    val booleanValue: Boolean get() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueBoolean

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

object ValueTrue : ValueBoolean(true) {
    override fun toString() = "true"
}

object ValueFalse : ValueBoolean(false) {
    override fun toString() = "false"
}