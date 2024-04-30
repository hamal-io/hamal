package io.hamal.lib.value

import io.hamal.lib.value.type.Type

sealed interface Value {
    val type: Type
}

sealed interface ValueComparable<VALUE_TYPE : Value> : Value, Comparable<VALUE_TYPE>

interface ValueVariable<VALUE_TYPE : Value> {

    val value: VALUE_TYPE

    abstract class BaseImpl<VALUE_TYPE : Value> : ValueVariable<VALUE_TYPE> {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BaseImpl<*>
            return value == other.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString() = "$value"
    }

    abstract class ComparableImpl<VALUE_TYPE : ValueComparable<VALUE_TYPE>> : BaseImpl<VALUE_TYPE>(),
        Comparable<ValueVariable<VALUE_TYPE>> {
        override fun compareTo(other: ValueVariable<VALUE_TYPE>): Int {
            return value.compareTo(other.value)
        }
    }
}

