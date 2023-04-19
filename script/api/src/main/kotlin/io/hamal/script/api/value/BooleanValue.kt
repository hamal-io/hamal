package io.hamal.script.api.value

import io.hamal.script.api.value.Value

object TrueValue : Value {
    override fun toString() = "true"
}

object FalseValue : Value {
    override fun toString() = "false"
}