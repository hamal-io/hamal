package io.hamal.lib.ddd.base


interface ValueObject<VALUE_TYPE : Any> {
    val value: VALUE_TYPE

    abstract class BaseImpl<VALUE_TYPE : Any>(
        override val value: VALUE_TYPE,
    ) : ValueObject<VALUE_TYPE> {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BaseImpl<*>
            return value == other.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString() = "${this.javaClass.simpleName}($value)"
    }

    abstract class ComparableImpl<VALUE_TYPE : Comparable<VALUE_TYPE>>(value: VALUE_TYPE) : BaseImpl<VALUE_TYPE>(value),
        Comparable<ValueObject<VALUE_TYPE>> {
        override fun compareTo(other: ValueObject<VALUE_TYPE>): Int {
            return value.compareTo(other.value)
        }
    }
}
