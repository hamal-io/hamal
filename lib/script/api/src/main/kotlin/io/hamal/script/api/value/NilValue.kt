package io.hamal.script.api.value

import io.hamal.script.api.value.Value

object NilValue : Value {
    override fun toString(): String {
        return "nil"
    }
}