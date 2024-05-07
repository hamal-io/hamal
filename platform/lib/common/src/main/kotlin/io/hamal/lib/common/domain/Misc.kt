package io.hamal.lib.common.domain

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueVariableNumber

class BatchSize(override val value: ValueNumber) : ValueVariableNumber() {

    init {
        require(value > ValueNumber(0)) { "BatchSize must be positive" }
    }

    companion object {
        val all = BatchSize(Int.MAX_VALUE)
        fun BatchSize(value: Int) = BatchSize(ValueNumber(value))
    }
}