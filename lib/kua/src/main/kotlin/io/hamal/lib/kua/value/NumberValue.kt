package io.hamal.lib.kua.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NumberValue")
data class NumberValue(
    val value: Double
) : SerializableValue {
    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = NumberValue(0.0)
        val One = NumberValue(1.0)
    }

    operator fun times(value: Int) = NumberValue(this.value * value)
    operator fun times(value: Double) = NumberValue(this.value * value)
}
