package io.hamal.module.worker.script.value

object TrueValue : Value {
    override fun toString(): String {
        return "true"
    }
}

object FalseValue : Value {
    override fun toString(): String {
        return "false"
    }
}