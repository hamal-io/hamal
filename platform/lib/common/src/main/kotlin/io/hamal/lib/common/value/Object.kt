package io.hamal.lib.common.value

import java.math.BigDecimal
import java.math.BigInteger

val TypeObject = ValueType("Object")

data class ValueObject(val values: LinkedHashMap<String, ValueSerializable>) : ValueSerializable {
    override val type get() = TypeObject

    fun stringValue(key: String): String = (get(key) as ValueString).stringValue

    operator fun get(identifier: String): Value = values[identifier] ?: ValueNil

    fun findBoolean(identifier: String): ValueBoolean? = values[identifier]?.let { it as ValueBoolean }
    fun getBoolean(identifier: String): ValueBoolean = findBoolean(identifier) ?: throw NoSuchElementException(identifier)

    fun findNumber(identifier: String): ValueNumber? = values[identifier]?.let { it as ValueNumber }
    fun getNumber(identifier: String): ValueNumber = findNumber(identifier) ?: throw NoSuchElementException(identifier)

    fun getSnowflakeId(identifier: String): ValueSnowflakeId = get(identifier) as ValueSnowflakeId

    fun findSerializable(identifier: String): ValueSerializable? = values[identifier]?.let { it as ValueSerializable }
    fun getSerializable(identifier: String): ValueSerializable = findSerializable(identifier) ?: throw NoSuchElementException(identifier)

    fun findString(identifier: String): ValueString? = values[identifier]?.let { it as ValueString }
    fun getString(identifier: String): ValueString = findString(identifier) ?: throw NoSuchElementException(identifier)

    fun findObject(identifier: String): ValueObject? = values[identifier]?.let { it as ValueObject }
    fun getObject(identifier: String): ValueObject = findObject(identifier) ?: throw NoSuchElementException(identifier)

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

    fun findNumber(identifier: String): ValueNumber? = value.findNumber(identifier)
    fun getNumber(identifier: String): ValueNumber = value.getNumber(identifier)

    fun findObject(identifier: String): ValueObject? = value.findObject(identifier)
    fun getObject(identifier: String): ValueObject = value.getObject(identifier)

    fun findSerializable(identifier: String): ValueSerializable? = value.findSerializable(identifier)
    fun getSerializable(identifier: String): ValueSerializable = value.getSerializable(identifier)

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