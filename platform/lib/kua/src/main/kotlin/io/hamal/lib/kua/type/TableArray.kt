package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class KuaTableArray(
    override val index: Int,
    override val state: State
) : KuaTable {

    override val type: KuaType.Type = KuaType.Type.Table

    fun asSequence(): Sequence<KuaType> {
        return KuaTableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index -> state.getAny(index).value }
        ).asSequence().map { it.value }
    }

    fun append(value: KuaType): Int {
        return when (value) {
            is KuaBoolean -> append(value)
            is KuaCode -> append(value)
            is KuaDecimal -> append(value)
            is KuaError -> append(value)
            is KuaFunction<*, *, *, *> -> append(value)
            is KuaNil -> append(value)
            is KuaNumber -> append(value)
            is KuaString -> append(value)
            is KuaTableArray -> append(value)
            is KuaTableMap -> append(value)
            else -> TODO()
        }
    }

    fun getTableMap(idx: Int): KuaTableMap {
        return state.getTableMap(idx)
    }

    fun getTableArray(idx: Int): KuaTableArray {
        return state.getTableArray(idx)
    }

    fun append(value: KuaAny): Int {
        TODO("Not yet implemented")
    }

    fun type(idx: Int): KClass<out KuaType> {
        TODO("Not yet implemented")
    }

    fun append(value: KuaString) = append(value.value)
    fun get(key: Int): KuaType {
        TODO("Not yet implemented")
    }


    val length get() = state.native.tableGetLength(index)

    fun getBoolean(idx: Int) = getBooleanType(idx) == KuaTrue
    fun getBooleanValue(idx: Int): KuaBoolean {
        TODO("Not yet implemented")
    }

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

    fun getDecimalType(idx: Int): KuaDecimal {
        TODO("Not yet implemented")
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


    fun append(value: String): Int {
        state.native.pushString(value)
        return state.tableAppend(index)
    }

    fun append(value: KuaTableMap): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }

    fun append(value: KuaTableArray): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }
}

private fun KClass<out KuaType>.checkExpectedType(expected: KClass<out KuaType>) {
    check(this == expected) {
        "Expected type to be $expected but was $this"
    }
}