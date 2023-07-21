package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue


class State(
    private val bridge: Bridge
) : AutoCloseable {

    val stack = Stack(bridge)

    fun push(value: NumberValue): StackSize {
        val result = bridge.pushNumber(value.value)
        return StackSize(result)
    }

    fun push(value: StringValue): StackSize {
        val result = bridge.pushString(value.value)
        return StackSize(result)
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}

