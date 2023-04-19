package io.hamal.script.api.value

data class StringValue(val value: String) : Value {
    override fun toString() = "'$value'"
}