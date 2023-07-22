package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.*

interface TableMapValue : Table {
    fun unset(key: String): TableLength
    fun unset(key: StringValue) = unset(key.value)
    operator fun set(key: String, value: NilValue) = unset(key)
    operator fun set(key: StringValue, value: NilValue) = unset(key.value)

    operator fun set(key: String, value: Boolean): TableLength
    operator fun set(key: String, value: BooleanValue) = set(key, value.value)
    operator fun set(key: StringValue, value: BooleanValue) = set(key.value, value.value)

    operator fun set(key: String, value: CodeValue): TableLength = set(key, value.value)
    operator fun set(key: StringValue, value: CodeValue): TableLength = set(key.value, value)

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long): TableLength = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: Double): TableLength
    operator fun set(key: String, value: NumberValue) = set(key, value.value)
    operator fun set(key: StringValue, value: NumberValue) = set(key.value, value.value)

    operator fun set(key: String, value: String): TableLength
    operator fun set(key: String, value: StringValue) = set(key, value.value)
    operator fun set(key: StringValue, value: StringValue) = set(key.value, value.value)

    fun getBooleanValue(key: String): BooleanValue
    fun getBooleanValue(key: StringValue): BooleanValue = getBooleanValue(key.value)
    fun getBoolean(key: String): Boolean = getBooleanValue(key).value
    fun getBoolean(key: StringValue): Boolean = getBoolean(key.value)

    fun getCodeValue(key: String): CodeValue
    fun getCodeValue(key: StringValue): CodeValue = getCodeValue(key.value)

    fun getNumberValue(key: String): NumberValue
    fun getNumberValue(key: StringValue): NumberValue = getNumberValue(key.value)
    fun getInt(key: String): Int = getNumberValue(key).value.toInt()
    fun getInt(key: StringValue) = getInt(key.value)
    fun getLong(key: String): Long = getNumberValue(key).value.toLong()
    fun getLong(key: StringValue): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
    fun getFloat(key: StringValue): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberValue(key).value
    fun getDouble(key: StringValue): Double = getDouble(key.value)

    fun getStringValue(key: String): StringValue
    fun getStringValue(key: StringValue): StringValue = getStringValue(key.value)
    fun getString(key: String): String = getStringValue(key).value
    fun getString(key: StringValue): String = getString(key.value)
}
