package io.hamal.lib.script.api.value

fun booleanOf(value: Boolean) = if (value) DepTrueValue else DepFalseValue

object DepTrueValue : DepValue {
    override val metaTable = DepMetaTableNotImplementedYet
    override fun toString() = "true"
}

object DepFalseValue : DepValue {
    override val metaTable = DepMetaTableNotImplementedYet
    override fun toString() = "false"
}