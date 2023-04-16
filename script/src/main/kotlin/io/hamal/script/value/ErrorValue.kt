package io.hamal.script.value

data class ErrorValue(
    val cause: StringValue
) : Value {
    constructor(cause: String) : this(StringValue(cause))
}