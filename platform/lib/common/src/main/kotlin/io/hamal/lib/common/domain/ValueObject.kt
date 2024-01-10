package io.hamal.lib.common.domain

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import java.time.Instant


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

        override fun toString() = "${this.javaClass.simpleName}($value)"
    }

    abstract class ComparableImpl<VALUE_TYPE : Comparable<VALUE_TYPE>> : BaseImpl<VALUE_TYPE>(),
        Comparable<ValueObject<VALUE_TYPE>> {
        override fun compareTo(other: ValueObject<VALUE_TYPE>): Int {
            return value.compareTo(other.value)
        }
    }
}

abstract class ValueObjectId : ValueObject.ComparableImpl<SnowflakeId>() {
    fun partition() = value.partition()
    fun sequence() = value.sequence()
    fun elapsed() = value.elapsed()
    override fun toString(): String {
        return "${this::class.simpleName}(${value.value})"
    }
}

abstract class ValueObjectString : ValueObject.ComparableImpl<String>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class ValueObjectInt : ValueObject.ComparableImpl<Int>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class ValueObjectInstant : ValueObject.ComparableImpl<Instant>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

abstract class ValueObjectMap : ValueObject.BaseImpl<HotObject>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}
