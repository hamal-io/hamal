package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeEnum : Type() {
    override val identifier = TypeIdentifier("Enum")
}

data class ValueEnum(val value: String) : Value {
    constructor(value: Enum<*>) : this(value.name)

    override val type get() = TypeEnum

    inline fun <reified T : Enum<T>> enumValue(): T = enumValueOf<T>(value)
    val stringValue: String get() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueEnum

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}


abstract class ValueVariableEnum : ValueVariable.BaseImpl<ValueEnum>() {
    inline fun <reified T : Enum<T>> enumValue(): T = value.enumValue()
    val stringValue: String get() = value.stringValue
}