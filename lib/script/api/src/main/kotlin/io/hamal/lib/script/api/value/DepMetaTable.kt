package io.hamal.lib.script.api.value

interface DepMetaTable {
    val type: String
    val operations: List<DepValueOperation>
}

object DepMetaTableNotImplementedYet : DepMetaTable {
    override val type: String
        get() = TODO("Not yet implemented")
    override val operations: List<DepValueOperation>
        get() = TODO("Not yet implemented")

}