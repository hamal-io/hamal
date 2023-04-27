package io.hamal.lib.vo.base

import io.hamal.lib.ddd.base.ValueObject
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
        require(value > 0) { "Version('$value') is illegal" }
    }
}