package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.throwIf
import kotlinx.serialization.Serializable

@Serializable
abstract class Version : ValueObject.ComparableImpl<Int>() {
    @Serializable
    data class Value(val value: Int) : Comparable<Value> {
        init {
            VersionValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }
}

internal object VersionValidator {
    fun validate(value: Int) {
        throwIf(value <= 0) { IllegalArgumentException("Version('$value') is illegal") }
    }
}