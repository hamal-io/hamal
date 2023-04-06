package io.hamal.lib.domain.vo

import io.hamal.lib.ddd.base.BaseComparableValueObject


abstract class Version(final override val value: Int) : BaseComparableValueObject<Int>() {
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
