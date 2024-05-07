package io.hamal.lib.common.value

val TypeBoolean = ValueType("Boolean")

data class ValueBoolean(private val value: Boolean) : ValueSerializable {
    constructor(value: String) : this(value.toBoolean())

    override val type get() = TypeBoolean

    val booleanValue: Boolean get() = value
    val stringValue: String get() = value.toString()

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

val ValueTrue = ValueBoolean(true)
val ValueFalse = ValueBoolean(false)

abstract class ValueVariableBoolean : ValueVariable.BaseImpl<ValueBoolean>() {
    val booleanValue: Boolean get() = value.booleanValue
}