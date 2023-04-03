package io.hamal.lib.domain.vo

import io.hamal.lib.ddd.base.BaseComparableValueObject
import io.hamal.lib.domain.vo.validator.IdValidator


abstract class Id(final override val value: String) : BaseComparableValueObject<String>() {
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
