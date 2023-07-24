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


internal data class TableProxyValue(
    override val index: Int,
    val state: State,
    override val type: TableType
) : TableMapProxyValue, TableArrayProxyValue {

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

    override fun getBooleanValue(key: String): BooleanValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Boolean)
        return booleanOf(bridge.toBoolean(-1)).also { bridge.pop(1) }
    }

    override fun getCodeValue(key: String): CodeValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return CodeValue(bridge.toString(-1)).also { bridge.pop(1) }
    }

    override fun getNumberValue(key: String): NumberValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.Number)
        return NumberValue(bridge.toNumber(-1)).also { bridge.pop(1) }
    }

    override fun getStringValue(key: String): StringValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return StringValue(bridge.toString(-1)).also { bridge.pop(1) }
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

    private val bridge = state.bridge
}

private fun ValueType.checkExpectedType(expected: ValueType) {
    check(this == expected) {
        "Expected type to be ${expected.toString().lowercase()} but was ${this.toString().lowercase()}"
    }
}