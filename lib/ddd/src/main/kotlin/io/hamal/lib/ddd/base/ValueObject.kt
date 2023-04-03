package io.hamal.lib.ddd.base

import java.io.Serializable

interface ValueObject<T> : Serializable {
    val value: T
}

interface ComparableValueObject<T : Comparable<T>> : ValueObject<T>, Comparable<ValueObject<T>> {
    val name: String
    override fun compareTo(other: ValueObject<T>): Int {
        return value.compareTo(other.value)
    }
}

abstract class BaseComparableValueObject<T : Comparable<T>> : ComparableValueObject<T> {
    override val name: String = javaClass.simpleName
    override fun toString(): String {
        return "$name ('$value')"
    }
}