package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class TableProxyArray(
    val index: Int,
    val state: State
) : KuaTable.Array {


    override val underlyingArray: Map<Int, KuaType>
        get() = TODO("Not yet implemented")

    override fun asSequence(): Sequence<KuaType> {
        return TableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index ->
                when (val value = state.getAny(index).value) {
//                is KuaTable.Array,
                    is KuaBoolean,
                    is KuaDecimal,
//                is KuaTable.Map,
                    is KuaNumber,
                    is KuaString -> value

                    is TableProxyMap -> state.toKuaTableMap(value)
                    is TableProxyArray -> state.toKuaTableArray(value)
                    else -> TODO("$value")
                }
            }
        ).asSequence().map { it.value }
    }

    override fun append(value: KuaTable): Int {
        TODO("Not yet implemented")
    }

    override fun append(value: KuaType): Int {
        return when (value) {
            is KuaString -> append(value)
            else -> TODO("Not yet implemented")
        }
    }

    override fun append(value: KuaAny): Int {
        TODO("Not yet implemented")
    }

    override fun type(idx: Int): KClass<out KuaType> {
        TODO("Not yet implemented")
    }

    override val size: Int = 0
    override val isArray: Boolean = true
    override val isMap: Boolean = false

    override fun append(value: KuaString) = append(value.value)
    override fun get(key: Int): KuaType {
        TODO("Not yet implemented")
    }


    override val type: KuaType.Type = KuaType.Type.Table

    val length get() = state.native.tableGetLength(index)

    override fun getBoolean(idx: Int) = getBooleanType(idx) == KuaTrue
    override fun getBooleanValue(idx: Int): KuaBoolean {
        TODO("Not yet implemented")
    }

    fun getBooleanType(idx: Int): KuaBoolean {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.getBooleanValue(-1)
    }

    override fun append(value: KuaBoolean) = append(value.value)

    override fun append(value: Boolean): Int {
        state.native.pushBoolean(value)
        return state.tableAppend(index)
    }


    override fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    override fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    override fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    override fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    override fun getNumberType(idx: Int): KuaNumber {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaNumber::class)
        return state.getNumberType(-1)
    }

    override fun getDecimalType(idx: Int): KuaDecimal {
        TODO("Not yet implemented")
    }

    override fun append(value: Int) = append(value.toDouble())
    override fun append(value: Long) = append(value.toDouble())
    override fun append(value: Float) = append(value.toDouble())
    override fun append(value: KuaNumber) = append(value.value)
    override fun append(value: Double): Int {
        state.native.pushNumber(value)
        return state.tableAppend(index)
    }

    override fun append(value: KuaDecimal): Int {
        state.native.pushDecimal(value)
        return state.tableAppend(index)
    }

    override fun append(value: ValueObjectId) = append(value.value.value)
    override fun append(value: SnowflakeId) = append(value.value.toString(16))

    override fun getString(idx: Int) = getStringType(idx).value
    override fun getStringType(idx: Int): KuaString {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaString::class)
        return state.getStringType(-1)
    }


    override fun append(value: String): Int {
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