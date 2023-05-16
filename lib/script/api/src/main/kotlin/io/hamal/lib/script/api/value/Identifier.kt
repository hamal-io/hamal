package io.hamal.lib.script.api.value

data class Identifier(
    val value: String
) : Value{
    override val metaTable = MetaTableNotImplementedYet
}