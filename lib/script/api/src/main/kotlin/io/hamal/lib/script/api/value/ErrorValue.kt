package io.hamal.lib.script.api.value

data class ErrorValue(
    val cause: StringValue
) : Value {
    override val metaTable = MetaTableNotImplementedYet
    constructor(cause: String) : this(StringValue(cause))

    override fun toString() = "Error: $cause"
}