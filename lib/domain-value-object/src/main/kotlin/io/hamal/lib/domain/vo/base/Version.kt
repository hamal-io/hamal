package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject

abstract class Version(value: Int) : ValueObject.ComparableImpl<Int>(value) {
    init {
        VersionValidator.validate(value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Version
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
