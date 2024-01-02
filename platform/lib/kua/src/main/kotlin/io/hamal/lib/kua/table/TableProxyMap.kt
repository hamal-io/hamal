package io.hamal.lib.kua.table

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass

class TableProxyMap(
    val index: Int,
    val state: State
) : Type { //FIXME not a type

    val length get() = state.native.tableGetLength(index)

    fun unset(key: StringType) = unset(key.value)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: String, value: NilType) = unset(key)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: StringType, value: NilType) = unset(key.value)
    fun unset(key: String): Int {
        state.pushString(key)
        state.pushNil()
        return state.tableSetRaw(index)
    }


    operator fun set(key: String, value: BooleanType) = set(key, value.value)
    operator fun set(key: StringType, value: BooleanType) = set(key.value, value.value)
    operator fun set(key: String, value: Boolean): Int {
        state.pushString(key)
        state.pushBoolean(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: CodeType) = set(key, value.value)
    operator fun set(key: StringType, value: CodeType) = set(key.value, value)

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long) = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: NumberType) = set(key, value.value)
    operator fun set(key: StringType, value: NumberType) = set(key.value, value.value)
    operator fun set(key: String, value: Double): Int {
        state.pushString(key)
        state.pushNumber(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: DecimalType): Int {
        state.pushString(key)
        state.native.pushDecimal(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
    operator fun set(key: StringType, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
    operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    operator fun set(key: StringType, value: SnowflakeId) = set(key.value, value.value.toString(16))

    operator fun set(key: String, value: StringType) = set(key, value.value)
    operator fun set(key: StringType, value: StringType) = set(key.value, value.value)
    operator fun set(key: String, value: String): Int {
        state.pushString(key)
        state.pushString(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: StringType, value: FunctionType<*, *, *, *>) = set(key.value, value)
    operator fun set(key: String, value: FunctionType<*, *, *, *>): Int {
        state.pushString(key)
        state.pushFunction(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: TableProxyMap): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    fun getTableMap(key: String): TableProxyMap {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(TableType::class)
        return state.getTableMapProxy(state.top.value)
    }

    fun getTableArray(key: String): TableProxyArray {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(TableType::class)
        return state.getTableArrayProxy(state.top.value)
    }

    operator fun set(key: String, value: TableProxyArray): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    fun getBooleanType(key: StringType): BooleanType = getBooleanType(key.value)
    fun getBoolean(key: String): Boolean = getBooleanType(key).value
    fun getBoolean(key: StringType): Boolean = getBoolean(key.value)
    fun getBooleanType(key: String): BooleanType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(BooleanType::class)
        return booleanOf(state.native.toBoolean(state.top.value)).also { state.native.pop(1) }
    }

    fun getCode(key: StringType): CodeType = getCode(key.value)
    fun getCode(key: String): CodeType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(StringType::class)
        return CodeType(state.getString(state.top.value)).also { state.native.pop(1) }
    }

    fun getNumberType(key: StringType): NumberType = getNumberType(key.value)
    fun getInt(key: String): Int = getNumberType(key).value.toInt()
    fun getInt(key: StringType) = getInt(key.value)
    fun getLong(key: String): Long = getNumberType(key).value.toLong()
    fun getLong(key: StringType): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberType(key).value.toFloat()
    fun getFloat(key: StringType): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberType(key).value
    fun getDouble(key: StringType): Double = getDouble(key.value)
    fun getNumberType(key: String): NumberType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(NumberType::class)
        return NumberType(state.native.toNumber(state.top.value)).also { state.native.pop(1) }
    }

    fun getDecimalType(key: String): DecimalType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(DecimalType::class)
        return state.native.toDecimal(state.top.value).also { state.native.pop(1) }
    }

    fun getStringType(key: StringType): StringType = getStringType(key.value)
    fun getString(key: String): String = getStringType(key).value
    fun getString(key: StringType): String = getString(key.value)
    fun getStringType(key: String): StringType {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(StringType::class)
        return StringType(state.native.toString(state.top.value)).also { state.native.pop(1) }
    }

    fun type(key: String): KClass<out Type> {
        state.pushString(key)
        return state.tableGetRaw(index)
    }
}

private fun KClass<out Type>.checkExpectedType(expected: KClass<out Type>) {
    check(this == expected) {
        "Expected type to be $expected but was $this"
    }
}