package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectString

class CorrelationId(override val value: String) : ValueObjectString() {
    companion object {
        val default = CorrelationId("__default__")
    }

    init {
        CorrelationIdValidator.validate(value)
    }

    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

internal object CorrelationIdValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("CorrelationId('$value') is illegal") }
    }
}
