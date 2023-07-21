package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

@JvmInline
value class StackSize(val value: Int)

class Stack internal constructor(
    internal val bridge: Bridge
) {
    fun isEmpty() = bridge.top() == 0
    fun isNotEmpty() = !isEmpty()
    fun size() = bridge.top()

    fun getNumber(idx: Int): NumberValue {
        val result = bridge.toNumber(idx)
        return NumberValue(result)
    }

    fun pushNumber(value: NumberValue): StackSize {
        val result = bridge.pushNumber(value.value)
        return StackSize(result)
    }

    fun getString(idx: Int): StringValue {
        val result = bridge.toString(idx)
        return StringValue(result)
    }

    fun pushString(value: StringValue): StackSize {
        val result = bridge.pushString(value.value)
        return StackSize(result)
    }

    fun <VALUE : Value> push(value: VALUE): StackSize {
        return when (value) {
            is NumberValue -> pushNumber(value)
            is StringValue -> pushString(value)
            else -> TODO()
        }
    }
}