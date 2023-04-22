package io.hamal.lib.domain.vo.base

import io.hamal.lib.ddd.base.ValueObject
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.throwIf
import kotlinx.serialization.Serializable

@Serializable
class RegionId(
    override val value: Value
) : ValueObject.ComparableImpl<RegionId.Value>() {
    constructor(value: String) : this(Value(value))

    @Serializable
    data class Value(val value: String) : Comparable<Value> {
        init {
            RegionIdValidator.validate(value)
        }

        override fun compareTo(other: Value) = value.compareTo(other.value)
    }
}

internal object RegionIdValidator {
    private val regex = Regex("^([A-Za-z0-9-_]{1,255})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Region('$value') is illegal") }
    }
}