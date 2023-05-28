package io.hamal.lib.script.api.value

data class DepIdentifier(
    val value: String
) : DepValue{
    override val metaTable = DepMetaTableNotImplementedYet
}