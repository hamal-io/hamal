package io.hamal.lib.kua.value.function

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value


val o = object {}

class FunctionContext(
    private val state: State
) {
    fun <VALUE : Value> push(value: VALUE) {
        println("pushing value $value")
        if (value is StringValue) {
            state.push(value)
        }
    }
}

