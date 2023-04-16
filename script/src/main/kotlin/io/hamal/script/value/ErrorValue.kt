package io.hamal.script.value

data class ErrorValue(
    val cause: StringValue
) : Value {
    constructor(cause: String) : this(StringValue(cause))

    override fun toString() = cause.value
}