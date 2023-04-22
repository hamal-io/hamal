package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.throwIf
import kotlinx.serialization.Serializable


@Serializable
abstract class Id : ValueObject.ComparableImpl<Id.Value>() {
    @Serializable
    data class Value(val value: String) : Comparable<Value> {
        init {
            IdValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }

}

internal object IdValidator {
    private val regex = Regex("^([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Id('$value') is illegal") }
    }
}