package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

data object TypeArray : Type() {
    override val identifier = TypeIdentifier("Array")
}

data class ValueArray(
    val value: List<Value>
) : Value {
    override val type get() = TypeArray

    operator fun get(idx: Int): Value = value[idx]

    fun asObject(idx: Int): ValueObject = get(idx) as ValueObject

    override fun toString() = value.toString()

    companion object {
        fun builder(): ValueArrayBuilder = ValueArrayBuilder()
    }
}

class ValueArrayBuilder {

    fun append(value: Enum<*>): ValueArrayBuilder = append(value.name)

    fun append(value: Value): ValueArrayBuilder {
        values.add(value)
        return this
    }

    fun append(value: String): ValueArrayBuilder {
        values.add(ValueString(value))
        return this
    }

    fun append(value: Byte): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Short): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: BigInteger): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: BigDecimal): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Int): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Float): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Double): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Long): ValueArrayBuilder {
        values.add(ValueNumber(value))
        return this
    }

    fun append(value: Boolean): ValueArrayBuilder {
        values.add(ValueBoolean(value))
        return this
    }

    fun build() = ValueArray(values)

    private val values = LinkedList<Value>()
}