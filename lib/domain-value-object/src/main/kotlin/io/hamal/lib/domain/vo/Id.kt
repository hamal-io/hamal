package io.hamal.lib.domain.vo

import io.hamal.lib.ddd.base.ValueObject


abstract class Id(value: String) : ValueObject.ComparableImpl<String>(value) {
    init {
        IdValidator.validate(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Id
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
