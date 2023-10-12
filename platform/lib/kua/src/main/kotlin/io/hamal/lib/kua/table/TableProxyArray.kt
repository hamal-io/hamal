package io.hamal.lib.kua.table

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.common.domain.DomainId
import io.hamal.lib.kua.State
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass

class TableProxyArray(
    val index: Int,
    val state: State
) : Type {

    val length get() = state.native.tableGetLength(index)

    fun getBoolean(idx: Int) = getBooleanType(idx) == True
    fun getBooleanType(idx: Int): BooleanType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(BooleanType::class)
        return state.getBooleanValue(-1)
    }

    fun append(value: BooleanType) = append(value.value)
    fun append(value: Boolean): Int {
        state.native.pushBoolean(value)
        return state.tableAppend(index)
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): NumberType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(NumberType::class)
        return state.getNumberType(-1)
    }

    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: NumberType) = append(value.value)
    fun append(value: Double): Int {
        state.native.pushNumber(value)
        return state.tableAppend(index)
    }

    fun append(value: DomainId) = append(value.value.value)
    fun append(value: SnowflakeId) = append(value.value.toString(16))

    fun getString(idx: Int) = getStringType(idx).value
    fun getStringType(idx: Int): StringType {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(StringType::class)
        return state.getStringType(-1)
    }

    fun append(value: StringType) = append(value.value)
    fun append(value: String): Int {
        state.native.pushString(value)
        return state.tableAppend(index)
    }

    fun append(value: TableProxyMap): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }

    fun append(value: TableProxyArray): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }
}

private fun KClass<out Type>.checkExpectedType(expected: KClass<out Type>) {
    check(this == expected) {
        "Expected type to be $expected but was $this"
    }
}