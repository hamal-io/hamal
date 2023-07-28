package io.hamal.lib.kua

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
    fun pushTable(proxy: TableMapProxyValue): StackTop
    fun pushTable(proxy: TableArrayProxyValue): StackTop
    fun getTable(idx: Int): TableValue
    fun getTableMap(idx: Int): TableMapProxyValue
    fun getTableArray(idx: Int): TableArrayProxyValue

    fun setGlobal(name: String, value: TableMapProxyValue)
    fun setGlobal(name: String, value: TableArrayProxyValue)
    fun getGlobalTableMap(name: String): TableMapProxyValue

    fun tableCreateMap(capacity: Int = 0): TableMapProxyValue
    fun tableCreateArray(capacity: Int = 0): TableArrayProxyValue
    fun tableInsert(idx: Int): TableLength
    fun tableSetRaw(idx: Int): TableLength
    fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): TableLength
    fun tableGetRaw(idx: Int): ValueType
    fun tableGetRawIdx(stackIdx: Int, tableIdx: Int): ValueType
}

class ClosableState(
    override val bridge: Bridge
) : State, AutoCloseable {
    override val top: StackTop get() = StackTop(bridge.top())


    override fun isEmpty() = bridge.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = bridge.setTop(idx)
    override fun pushTop(idx: Int): StackTop = StackTop(bridge.pushTop(idx))

    override fun type(idx: Int) = ValueType(bridge.type(idx))
    override fun pushNil() = StackTop(bridge.pushNil())

    override fun pushAny(value: AnyValue): StackTop {
        return when (val underlying = value.value) {
            is BooleanValue -> pushBoolean(underlying)
            is NumberValue -> pushNumber(underlying)
            is StringValue -> pushString(underlying)
            is TableArrayProxyValue -> pushTable(underlying)
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

    override fun pushTable(proxy: TableMapProxyValue) = StackTop(bridge.pushTop(proxy.index))

    override fun pushTable(proxy: TableArrayProxyValue) = StackTop(bridge.pushTop(proxy.index))

    override fun getTable(idx: Int): TableValue {
        TODO("Not yet implemented")
    }

    //FIXME type check
    override fun getTableMap(idx: Int): TableMapProxyValue = TableProxyValue(idx, this, TableType.Map)

    //FIXME type check
    override fun getTableArray(idx: Int): TableArrayProxyValue = TableProxyValue(idx, this, TableType.Array)

    override fun setGlobal(name: String, value: TableMapProxyValue) {
        bridge.pushTop(value.index)
        bridge.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableArrayProxyValue) {
        bridge.pushTop(value.index)
        bridge.setGlobal(name)
    }

    override fun getGlobalTableMap(name: String): TableMapProxyValue {
        bridge.getGlobal(name)
        return getTableMap(top.value)
    }

    override fun tableCreateMap(capacity: Int): TableMapProxyValue {
        return TableProxyValue(
            index = bridge.tableCreate(0, capacity),
            state = this,
            type = TableType.Map
        )
    }

    override fun tableCreateArray(capacity: Int): TableArrayProxyValue {
        return TableProxyValue(
            index = bridge.tableCreate(capacity, 0),
            state = this,
            type = TableType.Array
        )
    }

    override fun tableInsert(idx: Int) = TableLength(bridge.tableInsert(idx))
    override fun tableSetRaw(idx: Int) = TableLength(bridge.tableSetRaw(idx))
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = TableLength(bridge.tableSetRawIdx(stackIdx, tableIdx))
    override fun tableGetRaw(idx: Int) = ValueType.ValueType(bridge.tableGetRaw(idx))
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) =
        ValueType.ValueType(bridge.tableGetRawIdx(stackIdx, tableIdx))

    override fun close() {
        bridge.close()
    }
}

