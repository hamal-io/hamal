package io.hamal.lib.common.value

import io.hamal.lib.common.value.FieldIdentifier.Companion.FieldIdentifier
import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.math.BigDecimal
import java.math.BigInteger


data object TypeObject : Type() {
    override val identifier = TypeIdentifier("Object")
}

data class ValueObject(val values: LinkedHashMap<FieldIdentifier, Value>) : Value {
    override val type get() = TypeObject

    fun stringValue(key: String): String = (get(key) as ValueString).stringValue

    operator fun get(identifier: FieldIdentifier): Value = values[identifier] ?: ValueNil
    operator fun get(identifier: String): Value = get(FieldIdentifier(identifier))

    fun getNumber(identifier: FieldIdentifier): ValueNumber = get(identifier) as ValueNumber
    fun getNumber(identifier: String): ValueNumber = getNumber(FieldIdentifier(identifier))

    fun getSnowflakeId(identifier: FieldIdentifier): ValueSnowflakeId = get(identifier) as ValueSnowflakeId
    fun getSnowflakeId(identifier: String): ValueSnowflakeId = getSnowflakeId(FieldIdentifier(identifier))

    fun findString(identifier: FieldIdentifier): ValueString? = values[identifier]?.let { it as ValueString }
    fun findString(identifier: String): ValueString? = findString(FieldIdentifier(identifier))
    fun getString(identifier: FieldIdentifier): ValueString = findString(identifier) ?: throw NoSuchElementException(identifier.stringValue)
    fun getString(identifier: String): ValueString = getString(FieldIdentifier(identifier))

    fun getObject(identifier: FieldIdentifier): ValueObject = get(identifier) as ValueObject
    fun getObject(identifier: String): ValueObject = getObject(FieldIdentifier(identifier))

    override fun toString() = "${type.identifier}(${values.entries.joinToString(", ") { it.toString() }})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ValueObject

        return values == other.values
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    companion object {
        val empty = ValueObject(java.util.LinkedHashMap())
        fun builder(): ValueObjectBuilder = ValueObjectBuilder()
    }
}

abstract class ValueVariableObject : ValueVariable.BaseImpl<ValueObject>()

class ValueObjectBuilder {

    operator fun set(key: String, value: Enum<*>): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueString(value.name)
        return this
    }

    operator fun set(key: String, value: Value): ValueObjectBuilder {
        values[FieldIdentifier(key)] = value
        return this
    }

    operator fun set(key: String, value: ValueVariable<*>): ValueObjectBuilder {
        values[FieldIdentifier(key)] = value.value
        return this
    }

    operator fun set(key: String, value: String): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueString(value)
        return this
    }

    operator fun set(key: String, value: Byte): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Short): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigInteger): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigDecimal): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Int): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Float): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Double): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Long): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Boolean): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueBoolean(value)
        return this
    }

    fun setNil(key: String): ValueObjectBuilder {
        values[FieldIdentifier(key)] = ValueNil
        return this
    }

    fun build(): ValueObject {
        return ValueObject(values)
    }

    private val values = LinkedHashMap<FieldIdentifier, Value>()
}