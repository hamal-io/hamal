package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import kotlinx.serialization.Serializable

@Serializable
abstract class Reference : ValueObject.ComparableImpl<Reference.Value>() {
    @Serializable
    data class Value(val value: String) : Comparable<Value> {
        init {
            ReferenceValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }
}

internal object ReferenceValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("Reference('$value') is illegal") }
    }
}
