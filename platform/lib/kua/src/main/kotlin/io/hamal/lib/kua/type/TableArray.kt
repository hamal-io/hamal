package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class KuaTableArray(
    val index: Int,
    val state: State
) : KuaTableType {

    override val type: KuaType.Type = KuaType.Type.Table

    val underlyingArray: Map<Int, KuaType>
        get() = TODO("Not yet implemented")

    fun asSequence(): Sequence<KuaType> {
        return TableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index ->
                when (val value = state.getAny(index).value) {
//                is KuaTableArray,
                    is KuaBoolean,
                    is KuaDecimal,
//                is KuaTableMap,
                    is KuaNumber,
                    is KuaString -> value

                    is KuaTableMap -> value
                    is KuaTableArray -> value
                    else -> TODO("$value")
                }
            }
        ).asSequence().map { it.value }
    }


    fun append(value: KuaType): Int {
        return when (value) {
            is KuaString -> append(value)
            is KuaTableMap -> append(value)
            is KuaTableArray -> append(value)
            else -> TODO("Not yet implemented")
        }
    }

    fun append(value: KuaAny): Int {
        TODO("Not yet implemented")
    }

    fun type(idx: Int): KClass<out KuaType> {
        TODO("Not yet implemented")
    }

    val size: Int = 0
    val isArray: Boolean = true
    val isMap: Boolean = false

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