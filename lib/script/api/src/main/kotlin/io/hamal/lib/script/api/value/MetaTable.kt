package io.hamal.lib.script.api.value

interface MetaTable {
    val type: String
    val operations: List<ValueOperation>
}

object MetaTableNotImplementedYet : MetaTable {
    override val type: String
        get() = TODO("Not yet implemented")
    override val operations: List<ValueOperation>
        get() = TODO("Not yet implemented")

}