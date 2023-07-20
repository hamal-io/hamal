package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue


@JvmInline
value class StackSize(val value: Int)

class State(
    private val bridge: Bridge
) {
    fun push(value: NumberValue): StackSize {
        val result = bridge.pushNumber(value.value)
        return StackSize(result)
    }
}