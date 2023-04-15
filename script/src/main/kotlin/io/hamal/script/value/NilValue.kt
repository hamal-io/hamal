package io.hamal.script.value

object NilValue : Value {
    override fun toString(): String {
        return "nil"
    }
}