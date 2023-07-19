package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NumberValue")
data class NumberValue(
    val value: Double
) : Value {
    override val type = Value.Type.Number
    constructor(value: Int) : this(value.toDouble())
}
