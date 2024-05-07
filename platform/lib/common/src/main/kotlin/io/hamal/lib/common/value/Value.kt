package io.hamal.lib.common.value

data class ValueType(val value: String) {
    override fun toString(): String {
        return "Type$value"
    }
}

interface Value {
    val type: ValueType
}

sealed interface ValueSerializable : Value

interface ValueComparable<VALUE_TYPE : Value> : ValueSerializable, Comparable<VALUE_TYPE>

interface ValueVariable<VALUE_TYPE : ValueSerializable> : ValueSerializable {

    val value: VALUE_TYPE
    override val type: ValueType get() = value.type

    abstract class BaseImpl<VALUE_TYPE : ValueSerializable> : ValueVariable<VALUE_TYPE> {

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

