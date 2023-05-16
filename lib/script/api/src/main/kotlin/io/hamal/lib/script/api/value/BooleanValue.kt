package io.hamal.lib.script.api.value

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

object TrueValue : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString() = "true"
}

object FalseValue : Value {
    override val metaTable = MetaTableNotImplementedYet
    override fun toString() = "false"
}