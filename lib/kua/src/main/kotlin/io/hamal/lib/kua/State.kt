package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.table.*
import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.ValueType.Companion.ValueType

@JvmInline
value class StackTop(val value: Int)

interface State {
    //FIXME probably not a good idea to expose this internal - only for development / prototyping
    val bridge: Bridge
    val top: StackTop

    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun setTop(idx: Int)
    fun absIndex(idx: Int): Int
    fun pushTop(idx: Int): StackTop

    fun type(idx: Int): ValueType
    fun pushNil(): StackTop
    fun pushAny(value: AnyValue): StackTop
    fun getAnyValue(idx: Int): AnyValue
    fun pushBoolean(value: Boolean): StackTop
    fun pushBoolean(value: BooleanValue) = pushBoolean(value.value)
    fun getBoolean(idx: Int): Boolean
    fun getBooleanValue(idx: Int) = booleanOf(getBoolean(idx))
    fun pushError(value: ErrorValue): StackTop
    fun getNumber(idx: Int): Double
    fun getNumberValue(idx: Int) = NumberValue(getNumber(idx))
    fun pushNumber(value: Double): StackTop
    fun pushNumber(value: NumberValue) = pushNumber(value.value)

    fun getString(idx: Int): String
    fun getStringValue(idx: Int) = StringValue(getString(idx))
    fun pushString(value: String): StackTop
    fun pushString(value: StringValue) = pushString(value.value)

    fun pushTable(value: TableValue): StackTop
    fun pushTable(proxy: TableMapValue): StackTop
    fun pushTable(proxy: TableArrayValue): StackTop
    fun getTable(idx: Int): TableValue
    fun getTableMap(idx: Int): TableMapValue
    fun getTableArray(idx: Int): TableArrayValue

    fun setGlobal(name: String, value: FunctionValue<*, *, *, *>)
    fun setGlobal(name: String, value: TableMapValue)
    fun setGlobal(name: String, value: TableArrayValue)
    fun getGlobalTableMap(name: String): TableMapValue
    fun unsetGlobal(name: String)

    fun tableCreateMap(capacity: Int = 0): TableMapValue
    fun tableCreateArray(capacity: Int = 0): TableArrayValue
    fun tableAppend(idx: Int): TableLength
    fun tableSetRaw(idx: Int): TableLength
    fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): TableLength
    fun tableGetRaw(idx: Int): ValueType
    fun tableGetRawIdx(stackIdx: Int, tableIdx: Int): ValueType

    fun load(code: String) // FIXME add return value
}

class ClosableState(
    override val bridge: Bridge
) : State, AutoCloseable {
    override val top: StackTop get() = StackTop(bridge.top())


    override fun isEmpty() = bridge.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = bridge.setTop(idx)
    override fun absIndex(idx: Int) = bridge.absIndex(idx)

    override fun pushTop(idx: Int): StackTop = StackTop(bridge.pushTop(idx))

    override fun type(idx: Int) = ValueType(bridge.type(idx))
    override fun pushNil() = StackTop(bridge.pushNil())

    override fun pushAny(value: AnyValue): StackTop {
        return when (val underlying = value.value) {
            is BooleanValue -> pushBoolean(underlying)
            is NumberValue -> pushNumber(underlying)
            is StringValue -> pushString(underlying)
            is TableArrayValue -> pushTable(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAnyValue(idx: Int): AnyValue {
        return when (val type = type(idx)) {
            ValueType.Boolean -> AnyValue(getBooleanValue(idx))
            ValueType.Number -> AnyValue(getNumberValue(idx))
            ValueType.String -> AnyValue(getStringValue(idx))
            ValueType.Table -> AnyValue(getTableMap(idx)) // FIXME what about table and array ?
            else -> TODO("$type not supported yet")
        }
    }

    override fun pushBoolean(value: Boolean): StackTop = StackTop(bridge.pushBoolean(value))
    override fun getBoolean(idx: Int): Boolean = bridge.toBoolean(idx)
    override fun pushError(value: ErrorValue) = StackTop(bridge.pushError(value.message))

    override fun getNumber(idx: Int) = bridge.toNumber(idx)
    override fun pushNumber(value: Double) = StackTop(bridge.pushNumber(value))
    override fun getString(idx: Int) = bridge.toString(idx)
    override fun pushString(value: String) = StackTop(bridge.pushString(value))

    override fun pushTable(value: TableValue): StackTop {
        TODO("Not yet implemented")
    }

    override fun pushTable(proxy: TableMapValue) = StackTop(bridge.pushTop(proxy.index))

    override fun pushTable(proxy: TableArrayValue) = StackTop(bridge.pushTop(proxy.index))

    override fun getTable(idx: Int): TableValue {
        TODO("Not yet implemented")
    }

    //FIXME type check
    override fun getTableMap(idx: Int): TableMapValue = TableProxyValue(absIndex(idx), this, TableType.Map)

    //FIXME type check
    override fun getTableArray(idx: Int): TableArrayValue = TableProxyValue(absIndex(idx), this, TableType.Array)

    override fun setGlobal(name: String, value: FunctionValue<*, *, *, *>) {
        bridge.pushFunctionValue(value)
        bridge.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableMapValue) {
        bridge.pushTop(value.index)
        bridge.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableArrayValue) {
        bridge.pushTop(value.index)
        bridge.setGlobal(name)
    }

    override fun getGlobalTableMap(name: String): TableMapValue {
        bridge.getGlobal(name)
        return getTableMap(top.value)
    }

    override fun unsetGlobal(name: String) {
        bridge.pushNil()
        bridge.setGlobal(name)
    }

    override fun tableCreateMap(capacity: Int): TableMapValue {
        return TableProxyValue(
            index = bridge.tableCreate(0, capacity),
            state = this,
            type = TableType.Map
        )
    }

    override fun tableCreateArray(capacity: Int): TableArrayValue {
        return TableProxyValue(
            index = bridge.tableCreate(capacity, 0),
            state = this,
            type = TableType.Array
        )
    }

    override fun tableAppend(idx: Int) = TableLength(bridge.tableAppend(idx))
    override fun tableSetRaw(idx: Int) = TableLength(bridge.tableSetRaw(idx))
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = TableLength(bridge.tableSetRawIdx(stackIdx, tableIdx))
    override fun tableGetRaw(idx: Int) = ValueType.ValueType(bridge.tableGetRaw(idx))
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) =
        ValueType.ValueType(bridge.tableGetRawIdx(stackIdx, tableIdx))

    override fun load(code: String) {
        bridge.load(code)
    }

    override fun close() {
        bridge.close()
    }
}

