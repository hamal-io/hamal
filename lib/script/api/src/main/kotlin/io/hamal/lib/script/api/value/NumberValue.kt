package io.hamal.lib.script.api.value

import io.hamal.lib.common.math.Decimal

data class NumberValue(val value: Decimal) : Value {
    constructor(value: String) : this(Decimal(value))
    constructor(value: Int) : this(Decimal(value))
    override fun toString() = value.toString()
}