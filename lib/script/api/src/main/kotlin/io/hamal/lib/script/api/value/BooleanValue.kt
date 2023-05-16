package io.hamal.lib.script.api.value

object TrueValue : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString() = "true"
}

object FalseValue : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString() = "false"
}