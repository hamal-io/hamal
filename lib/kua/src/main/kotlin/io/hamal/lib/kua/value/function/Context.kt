package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

class Context(
    private val bridge: Bridge
) {
    fun <VALUE : Value> push(value: VALUE) {
        println("pushing value $value")
        if (value is StringValue) {
            bridge.pushString(value.value)
        }
    }
}