package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.base.IdValidator
import kotlinx.serialization.Serializable


@Serializable
//sealed class Id(value: String) : ValueObject.ComparableImpl<String>(value) {
sealed class Id {
    abstract val value: String

    init {
//        IdValidator.validate(value)
    }

    protected fun validate(){ IdValidator.validate(value) }

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
