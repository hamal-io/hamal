package io.hamal.lib.kua

import io.hamal.lib.kua.table.TableLength
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value
import io.hamal.lib.kua.value.ValueType
import io.hamal.lib.kua.value.ValueType.Companion.ValueType

@JvmInline
value class StackSize(val value: Int)

interface State {
    //FIXME probably not a good idea to expose this internal - only for development / prototyping
    val bridge: Bridge

    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun length(): Int
    fun setTop(idx: Int)

    fun type(idx: Int): ValueType

    fun getNumber(idx: Int): Double
    fun getNumberValue(idx: Int) = NumberValue(getNumber(idx))
    fun pushNumber(value: Double): StackSize
    fun pushNumber(value: NumberValue) = pushNumber(value.value)

    fun getString(idx: Int): String
    fun getStringValue(idx: Int) = StringValue(getString(idx))
    fun pushString(value: String): StackSize
    fun pushString(value: StringValue) = pushString(value.value)

    fun <VALUE : Value> push(value: VALUE) = when (value) {
        is NumberValue -> pushNumber(value)
        is StringValue -> pushString(value)
        else -> TODO()
    }

    fun tableSetRaw(idx: Int): TableLength
    fun tableGetRaw(idx: Int): ValueType
}

class ClosableState(
    override val bridge: Bridge
) : State, AutoCloseable {

    override fun isEmpty() = bridge.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun length() = bridge.top()
    override fun setTop(idx: Int) = bridge.setTop(idx)

    override fun getNumber(idx: Int) = bridge.toNumber(idx)
    override fun pushNumber(value: Double) = StackSize(bridge.pushNumber(value))

    override fun getString(idx: Int) = bridge.toString(idx)
    override fun pushString(value: String) = StackSize(bridge.pushString(value))

    override fun type(idx: Int): ValueType {
        return ValueType(bridge.type(idx))
    }

    override fun tableSetRaw(idx: Int) = TableLength(bridge.tableSetRaw(idx))
    override fun tableGetRaw(idx: Int) = ValueType.ValueType(bridge.tableGetRaw(idx))

    override fun close() {
        TODO("Not yet implemented")
    }
}

