package io.hamal.lib.kua.table

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass

class TableProxyArray(
    val index: Int,
    val state: State
) : KuaTableType {

    override val type: KuaType.Type = KuaType.Type.Array

    val length get() = state.native.tableGetLength(index)

    fun getBoolean(idx: Int) = getBooleanType(idx) == KuaTrue
    fun getBooleanType(idx: Int): KuaBoolean {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.getBooleanValue(-1)
    }

    fun append(value: KuaBoolean) = append(value.value)
    fun append(value: Boolean): Int {
        state.native.pushBoolean(value)
        return state.tableAppend(index)
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): KuaNumber {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaNumber::class)
        return state.getNumberType(-1)
    }

    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: KuaNumber) = append(value.value)
    fun append(value: Double): Int {
        state.native.pushNumber(value)
        return state.tableAppend(index)
    }

    fun append(value: KuaDecimal): Int {
        state.native.pushDecimal(value)
        return state.tableAppend(index)
    }

    fun append(value: ValueObjectId) = append(value.value.value)
    fun append(value: SnowflakeId) = append(value.value.toString(16))

    fun getString(idx: Int) = getStringType(idx).value
    fun getStringType(idx: Int): KuaString {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaString::class)
        return state.getStringType(-1)
    }

    fun append(value: KuaString) = append(value.value)
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

private fun KClass<out KuaType>.checkExpectedType(expected: KClass<out KuaType>) {
    check(this == expected) {
        "Expected type to be $expected but was $this"
    }
}