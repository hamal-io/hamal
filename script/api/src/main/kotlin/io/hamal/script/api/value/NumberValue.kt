package io.hamal.script.api.value

import io.hamal.lib.meta.math.Decimal

data class NumberValue(val value: Decimal) : Value {
    constructor(value: String) : this(Decimal(value))
    constructor(value: Int) : this(Decimal(value))
    override fun toString() = value.toString()
}