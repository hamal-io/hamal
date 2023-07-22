package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.ValueType.Number

@JvmInline
value class TableLength(val value: Int)

internal data class TableProxyContext(
    val index: Int,
    val state: State
)

internal class TableProxy(
    ctx: TableProxyContext
) : TableMap, TableArray {
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
        type.checkExpectedType(Number)
        return NumberValue(bridge.toNumber(-1)).also { bridge.pop(1) }
    }

    override fun getStringValue(key: String): StringValue {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(ValueType.String)
        return StringValue(bridge.toString(-1)).also { bridge.pop(1) }
    }


    fun length(): TableLength = TableLength((bridge.tableGetLength(index)))

    private val index = ctx.index
    private val state = ctx.state
    private val bridge = ctx.state.bridge
}

private fun ValueType.checkExpectedType(expected: ValueType) {
    check(this == expected) {
        "Expected type to be ${expected.toString().lowercase()} but was ${this.toString().lowercase()}"
    }
}