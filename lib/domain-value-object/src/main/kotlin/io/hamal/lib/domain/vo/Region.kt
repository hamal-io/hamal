package io.hamal.lib.domain.vo

import io.hamal.lib.ddd.base.ValueObject

class Region(value: String) : ValueObject.ComparableImpl<String>(value) {

    init {
        ReferenceValidator.validate(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Region
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
