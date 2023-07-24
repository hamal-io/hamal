package io.hamal.lib.kua

import io.hamal.lib.kua.table.TableLength
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
    fun pushAny(value: AnyValue): StackTop
    fun getAnyValue(idx: Int): AnyValue
    fun pushBoolean(value: Boolean): StackTop
    fun pushBoolean(value: BooleanValue) = pushBoolean(value.value)
    fun getBoolean(idx: Int): Boolean
    fun getBooleanValue(idx: Int) = booleanOf(getBoolean(idx))
    fun getNumber(idx: Int): Double
    fun getNumberValue(idx: Int) = NumberValue(getNumber(idx))
    fun pushNumber(value: Double): StackTop
    fun pushNumber(value: NumberValue) = pushNumber(value.value)

    fun getString(idx: Int): String
    fun getStringValue(idx: Int) = StringValue(getString(idx))
    fun pushString(value: String): StackTop
    fun pushString(value: StringValue) = pushString(value.value)

    fun tableInsert(idx: Int): TableLength
    fun tableSetRaw(idx: Int): TableLength
    fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): TableLength
    fun tableGetRaw(idx: Int): ValueType
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
    override fun pushAny(value: AnyValue): StackTop {
        return when (val underlying = value.value) {
            is BooleanValue -> pushBoolean(underlying)
            is NumberValue -> pushNumber(underlying)
            is StringValue -> pushString(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAnyValue(idx: Int): AnyValue {
        return when (val type = type(idx)) {
            ValueType.Boolean -> AnyValue(getBooleanValue(idx))
            ValueType.Number -> AnyValue(getNumberValue(idx))
            ValueType.String -> AnyValue(getStringValue(idx))
            else -> TODO("$type not supported yet")
        }
    }

    override fun pushBoolean(value: Boolean): StackTop = StackTop(bridge.pushBoolean(value))
    override fun getBoolean(idx: Int): Boolean = bridge.toBoolean(idx)
    override fun getNumber(idx: Int) = bridge.toNumber(idx)
    override fun pushNumber(value: Double) = StackTop(bridge.pushNumber(value))
    override fun getString(idx: Int) = bridge.toString(idx)
    override fun pushString(value: String) = StackTop(bridge.pushString(value))

    override fun tableInsert(idx: Int) = TableLength(bridge.tableInsert(idx))
    override fun tableSetRaw(idx: Int) = TableLength(bridge.tableSetRaw(idx))
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = TableLength(bridge.tableSetRawIdx(stackIdx, tableIdx))
    override fun tableGetRaw(idx: Int) = ValueType.ValueType(bridge.tableGetRaw(idx))

    override fun close() {
        bridge.close()
    }
}

