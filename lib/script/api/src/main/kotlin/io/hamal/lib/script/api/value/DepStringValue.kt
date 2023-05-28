package io.hamal.lib.script.api.value

data class DepStringValue(val value: String) : DepValue {
    override val metaTable = DepMetaTableNotImplementedYet
    override fun toString() = "'$value'"
}