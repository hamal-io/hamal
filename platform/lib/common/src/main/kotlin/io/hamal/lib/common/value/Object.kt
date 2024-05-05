package io.hamal.lib.common.value

import java.math.BigDecimal
import java.math.BigInteger

val TypeObject = ValueType("Object")

data class ValueObject(val values: LinkedHashMap<String, ValueSerializable>) : ValueSerializable {
    override val type get() = TypeObject

    fun stringValue(key: String): String = (get(key) as ValueString).stringValue

    operator fun get(identifier: String): Value = values[identifier] ?: ValueNil

    fun getNumber(identifier: String): ValueNumber = get(identifier) as ValueNumber

    fun getSnowflakeId(identifier: String): ValueSnowflakeId = get(identifier) as ValueSnowflakeId

    fun findString(identifier: String): ValueString? = values[identifier]?.let { it as ValueString }
    fun getString(identifier: String): ValueString = findString(identifier) ?: throw NoSuchElementException(identifier)

    fun getObject(identifier: String): ValueObject = get(identifier) as ValueObject

    override fun toString() = values.entries.joinToString(", ") { it.toString() }

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

abstract class ValueVariableObject : ValueVariable.BaseImpl<ValueObject>() {
    fun findString(identifier: String): ValueString? = value.findString(identifier)
    fun getString(identifier: String): ValueString = value.getString(identifier)
}

class ValueObjectBuilder {

    operator fun set(key: String, value: Enum<*>): ValueObjectBuilder {
        values[key] = ValueString(value.name)
        return this
    }

    operator fun set(key: String, value: ValueSerializable): ValueObjectBuilder {
        values[key] = value
        return this
    }

    operator fun set(key: String, value: ValueVariable<*>): ValueObjectBuilder {
        values[key] = value.value
        return this
    }

    operator fun set(key: String, value: String): ValueObjectBuilder {
        values[key] = ValueString(value)
        return this
    }

    operator fun set(key: String, value: Byte): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Short): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigInteger): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: BigDecimal): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Int): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Float): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Double): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Long): ValueObjectBuilder {
        values[key] = ValueNumber(value)
        return this
    }

    operator fun set(key: String, value: Boolean): ValueObjectBuilder {
        values[key] = ValueBoolean(value)
        return this
    }

    fun setNil(key: String): ValueObjectBuilder {
        values[key] = ValueNil
        return this
    }

    fun build(): ValueObject {
        return ValueObject(values)
    }

    private val values = LinkedHashMap<String, ValueSerializable>()
}