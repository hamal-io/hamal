package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.table.*
import io.hamal.lib.kua.value.*
import io.hamal.lib.kua.value.ValueType.Companion.ValueType

@JvmInline
value class StackTop(val value: Int)

interface State {
    //FIXME probably not a good idea to expose this internal - only for development / prototyping
    val native: Native
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
    fun pushFunction(value: FunctionValue<*, *, *, *>): StackTop

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
    override val native: Native
) : State, AutoCloseable {
    override val top: StackTop get() = StackTop(native.top())


    override fun isEmpty() = native.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = native.setTop(idx)
    override fun absIndex(idx: Int) = native.absIndex(idx)

    override fun pushTop(idx: Int): StackTop = StackTop(native.pushTop(idx))

    override fun type(idx: Int) = ValueType(native.type(idx))
    override fun pushNil() = StackTop(native.pushNil())

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

    override fun pushBoolean(value: Boolean): StackTop = StackTop(native.pushBoolean(value))
    override fun getBoolean(idx: Int): Boolean = native.toBoolean(idx)
    override fun pushError(value: ErrorValue) = StackTop(native.pushError(value.message))
    override fun pushFunction(value: FunctionValue<*, *, *, *>) = StackTop(native.pushFunctionValue(value))

    override fun getNumber(idx: Int) = native.toNumber(idx)
    override fun pushNumber(value: Double) = StackTop(native.pushNumber(value))
    override fun getString(idx: Int) = native.toString(idx)
    override fun pushString(value: String) = StackTop(native.pushString(value))

    override fun pushTable(value: TableValue): StackTop {
        TODO("Not yet implemented")
    }

    override fun pushTable(proxy: TableMapValue) = StackTop(native.pushTop(proxy.index))

    override fun pushTable(proxy: TableArrayValue) = StackTop(native.pushTop(proxy.index))

    override fun getTable(idx: Int): TableValue {
        TODO("Not yet implemented")
    }

    //FIXME type check
    override fun getTableMap(idx: Int): TableMapValue = TableProxyValue(absIndex(idx), this, TableType.Map)

    //FIXME type check
    override fun getTableArray(idx: Int): TableArrayValue = TableProxyValue(absIndex(idx), this, TableType.Array)

    override fun setGlobal(name: String, value: FunctionValue<*, *, *, *>) {
        native.pushFunctionValue(value)
        native.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableMapValue) {
        native.pushTop(value.index)
        native.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableArrayValue) {
        native.pushTop(value.index)
        native.setGlobal(name)
    }

    override fun getGlobalTableMap(name: String): TableMapValue {
        native.getGlobal(name)
        return getTableMap(top.value)
    }

    override fun unsetGlobal(name: String) {
        native.pushNil()
        native.setGlobal(name)
    }

    override fun tableCreateMap(capacity: Int): TableMapValue {
        return TableProxyValue(
            index = native.tableCreate(0, capacity),
            state = this,
            type = TableType.Map
        )
    }

    override fun tableCreateArray(capacity: Int): TableArrayValue {
        return TableProxyValue(
            index = native.tableCreate(capacity, 0),
            state = this,
            type = TableType.Array
        )
    }

    override fun tableAppend(idx: Int) = TableLength(native.tableAppend(idx))
    override fun tableSetRaw(idx: Int) = TableLength(native.tableSetRaw(idx))
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = TableLength(native.tableSetRawIdx(stackIdx, tableIdx))
    override fun tableGetRaw(idx: Int) = ValueType.ValueType(native.tableGetRaw(idx))
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) =
        ValueType.ValueType(native.tableGetRawIdx(stackIdx, tableIdx))

    override fun load(code: String) {
        native.load(code)
    }

    override fun close() {
        native.close()
    }
}

