package io.hamal.lib.script.api.value

import io.hamal.lib.common.math.Decimal
import java.math.BigInteger

data class NumberValue(val value: Decimal) : Value {
    constructor(value: String) : this(Decimal(value))
    constructor(value: Int) : this(Decimal(value))
    constructor(value: BigInteger) : this(Decimal(value.toBigDecimal()))

    override fun toString() = value.toString()
}