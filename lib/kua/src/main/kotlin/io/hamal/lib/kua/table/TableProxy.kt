package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.*

@JvmInline
value class TableLength(val value: Int)

interface BaseTableProxyValue : Value {
    val index: Int
    val type: TableType

    fun length(): TableLength
}

enum class TableType {
    Array,
    Map
}


data class TableProxyValue(
    override val index: Int,
    val state: State,
    override val type: TableType
) : TableMapValue, TableArrayValue {

    override fun unset(key: String): TableLength {
        bridge.pushString(key)
        bridge.pushNil()
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: Boolean): TableLength {
        bridge.pushString(key)
        bridge.pushBoolean(value)
        return state.tableSetRaw(index)
    }


    override fun set(key: String, value: Double): TableLength {
        bridge.pushString(key)
        bridge.pushNumber(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: String): TableLength {
        state.pushString(key)
        state.pushString(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: TableMapValue): TableLength {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    override fun set(key: String, value: TableArrayValue): TableLength {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    override fun getBooleanValue(key: String): BooleanValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Boolean)
        return booleanOf(bridge.toBoolean(state.top.value)).also { bridge.pop(1) }
    }

    override fun getCodeValue(key: String): CodeValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return CodeValue(bridge.toString(state.top.value)).also { bridge.pop(1) }
    }

    override fun getNumberValue(key: String): NumberValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Number)
        return NumberValue(bridge.toNumber(state.top.value)).also { bridge.pop(1) }
    }

    override fun getStringValue(key: String): StringValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return StringValue(bridge.toString(state.top.value)).also { bridge.pop(1) }
    }

    override fun getTableMap(key: String): TableMapValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Table)
        return state.getTableMap(state.top.value)
    }

    override fun length(): TableLength = TableLength((bridge.tableGetLength(index)))

    override fun append(value: Boolean): TableLength {
        bridge.pushBoolean(value)
        return state.tableInsert(index)
    }

    override fun append(value: Double): TableLength {
        bridge.pushNumber(value)
        return state.tableInsert(index)
    }

    override fun append(value: String): TableLength {
        bridge.pushString(value)
        return state.tableInsert(index)
    }

    override fun append(value: TableMapValue): TableLength {
        state.pushTable(value)
        return state.tableInsert(index)
    }

    override fun append(value: TableArrayValue): TableLength {
        state.pushTable(value)
        return state.tableInsert(index)
    }

    override fun get(idx: Int): AnyValue {
        TODO("Not yet implemented")
    }

    override fun getBooleanValue(idx: Int): BooleanValue {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.Boolean)
        return state.getBooleanValue(-1)
    }

    override fun getNumberValue(idx: Int): NumberValue {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.Number)
        return state.getNumberValue(-1)
    }

    override fun getStringValue(idx: Int): StringValue {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(ValueType.String)
        return state.getStringValue(-1)
    }

    private val bridge = state.bridge
}

private fun ValueType.checkExpectedType(expected: ValueType) {
    check(this == expected) {
        "Expected type to be ${expected.toString().lowercase()} but was ${this.toString().lowercase()}"
    }
}