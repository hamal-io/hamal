package io.hamal.lib.script.api.value

data class DepErrorValue(
    val cause: DepStringValue
) : DepValue {
    override val metaTable = DepMetaTableNotImplementedYet
    constructor(cause: String) : this(DepStringValue(cause))

    override fun toString() = "Error: $cause"
}