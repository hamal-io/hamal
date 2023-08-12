package io.hamal.lib.kua.table

import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.type.*


interface TableMap : TableProxy {
    fun unset(key: String): TableLength
    fun unset(key: StringType) = unset(key.value)
    operator fun set(key: String, value: NilType) = unset(key)
    operator fun set(key: StringType, value: NilType) = unset(key.value)

    operator fun set(key: String, value: Boolean): TableLength
    operator fun set(key: String, value: BooleanType) = set(key, value.value)
    operator fun set(key: StringType, value: BooleanType) = set(key.value, value.value)

    operator fun set(key: String, value: CodeType): TableLength = set(key, value.value)
    operator fun set(key: StringType, value: CodeType): TableLength = set(key.value, value)

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long): TableLength = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: Double): TableLength
    operator fun set(key: String, value: DoubleType) = set(key, value.value)
    operator fun set(key: StringType, value: DoubleType) = set(key.value, value.value)

    operator fun set(key: String, value: String): TableLength
    operator fun set(key: String, value: StringType) = set(key, value.value)
    operator fun set(key: StringType, value: StringType) = set(key.value, value.value)

    operator fun set(key: String, value: FunctionType<*, *, *, *>): TableLength
    operator fun set(key: StringType, value: FunctionType<*, *, *, *>) = set(key.value, value)

    operator fun set(key: String, value: TableMap): TableLength
    operator fun set(key: String, value: TableArray): TableLength

    fun getBooleanValue(key: String): BooleanType
    fun getBooleanValue(key: StringType): BooleanType = getBooleanValue(key.value)
    fun getBoolean(key: String): Boolean = getBooleanValue(key).value
    fun getBoolean(key: StringType): Boolean = getBoolean(key.value)

    fun getCodeValue(key: String): CodeType
    fun getCodeValue(key: StringType): CodeType = getCodeValue(key.value)

    fun getNumberValue(key: String): DoubleType
    fun getNumberValue(key: StringType): DoubleType = getNumberValue(key.value)
    fun getInt(key: String): Int = getNumberValue(key).value.toInt()
    fun getInt(key: StringType) = getInt(key.value)
    fun getLong(key: String): Long = getNumberValue(key).value.toLong()
    fun getLong(key: StringType): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
    fun getFloat(key: StringType): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberValue(key).value
    fun getDouble(key: StringType): Double = getDouble(key.value)

    fun getStringValue(key: String): StringType
    fun getStringValue(key: StringType): StringType = getStringValue(key.value)
    fun getString(key: String): String = getStringValue(key).value
    fun getString(key: StringType): String = getString(key.value)

    fun getTableMap(key: String): TableMap
}
