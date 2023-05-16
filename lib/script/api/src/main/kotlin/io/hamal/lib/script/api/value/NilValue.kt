package io.hamal.lib.script.api.value

object NilValue : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString(): String {
        return "nil"
    }
}