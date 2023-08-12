package io.hamal.lib.kua.table

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.kua.type.*

interface TableArray : TableProxy {
    fun append(value: Boolean): Int
    fun append(value: BooleanType) = append(value.value)

    fun append(value: Double): Int
    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: DoubleType) = append(value.value)

    fun append(value: SnowflakeId) = append(value.value.toString(16))
    fun append(value: DomainId) = append(value.value.value)

    fun append(value: String): Int
    fun append(value: StringType) = append(value.value)

    fun append(value: TableMap): Int
    fun append(value: TableArray): Int

    fun get(idx: Int): AnyType
    fun getBoolean(idx: Int) = getBooleanValue(idx) == TrueValue
    fun getBooleanValue(idx: Int): BooleanType
    fun getNumberValue(idx: Int): DoubleType
    fun getInt(idx: Int) = getNumberValue(idx).value.toInt()
    fun getLong(idx: Int) = getNumberValue(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberValue(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberValue(idx).value.toDouble()
    fun getString(idx: Int) = getStringValue(idx).value
    fun getStringValue(idx: Int): StringType
}