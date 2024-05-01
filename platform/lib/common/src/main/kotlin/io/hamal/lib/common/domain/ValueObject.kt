package io.hamal.lib.common.domain

import io.hamal.lib.common.hot.HotObject


interface ValueObject<VALUE_TYPE : Any> {

    val value: VALUE_TYPE

    abstract class BaseImpl<VALUE_TYPE : Any> : ValueObject<VALUE_TYPE> {

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

    abstract class ComparableImpl<VALUE_TYPE : Comparable<VALUE_TYPE>> : BaseImpl<VALUE_TYPE>(),
        Comparable<ValueObject<VALUE_TYPE>> {
        override fun compareTo(other: ValueObject<VALUE_TYPE>): Int {
            return value.compareTo(other.value)
        }
    }
}

abstract class ValueObjectHotObject : ValueObject.BaseImpl<HotObject>()
