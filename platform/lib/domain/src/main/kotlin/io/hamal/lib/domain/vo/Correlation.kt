package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString


class CorrelationId(override val value: ValueString) : ValueVariableString() {
    companion object {
        fun CorrelationId(value: String) = CorrelationId(ValueString(value))
        val default = CorrelationId("__default__")
    }

    init {
        CorrelationIdValidator.validate(value.stringValue)
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
