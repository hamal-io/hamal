package io.hamal.lib.common.domain

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueVariableNumber

class Limit(override val value: ValueNumber) : ValueVariableNumber() {
    init {
        require(value > ValueNumber(0)) { "Limit must be positive" }
    }

    companion object {
        val one = Limit(1)
        val all = Limit(Int.MAX_VALUE)
        fun Limit(value: Int) = Limit(ValueNumber(value))
    }
}


class Count(override val value: ValueNumber) : ValueVariableNumber() {

    init {
        require(value >= ValueNumber(0)) { "Count must not be negative" }
    }

    companion object {
        val None = Count(0)
        fun Count(value: Int) = Count(value.toLong())
        fun Count(value: Long) = Count(ValueNumber(value))
    }
}
