package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.*

interface TableArrayValue : BaseTableProxyValue {
    fun append(value: Boolean): TableLength
    fun append(value: BooleanValue) = append(value.value)

    fun append(value: Double): TableLength
    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: NumberValue) = append(value.value)

    fun append(value: String): TableLength
    fun append(value: StringValue) = append(value.value)

    fun append(value: TableMapValue): TableLength
    fun append(value: TableArrayValue): TableLength

    fun get(idx: Int): AnyValue
    fun getBoolean(idx: Int) = getBooleanValue(idx) == TrueValue
    fun getBooleanValue(idx: Int): BooleanValue
    fun getNumberValue(idx: Int): NumberValue
    fun getInt(idx: Int) = getNumberValue(idx).value.toInt()
    fun getLong(idx: Int) = getNumberValue(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberValue(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberValue(idx).value.toDouble()
    fun getString(idx: Int) = getStringValue(idx).value
    fun getStringValue(idx: Int): StringValue
}