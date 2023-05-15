package io.hamal.lib.script.api.value

data class ErrorValue(
    val cause: StringValue
) : Value {
    constructor(cause: String) : this(StringValue(cause))

    override fun toString() = "Error: $cause"
}