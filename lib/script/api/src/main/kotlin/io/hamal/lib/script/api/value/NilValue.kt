package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.Value

object NilValue : Value {
    override fun toString(): String {
        return "nil"
    }
}