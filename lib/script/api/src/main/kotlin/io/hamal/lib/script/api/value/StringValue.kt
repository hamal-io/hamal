package io.hamal.lib.script.api.value

data class StringValue(val value: String) : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString() = "'$value'"
}