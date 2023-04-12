package io.hamal.module.worker.script.value

data class StringValue(val value: String) : Value {
    override fun toString(): String {
        return "'$value'"
    }
}