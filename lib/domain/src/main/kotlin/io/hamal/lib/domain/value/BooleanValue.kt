package io.hamal.lib.domain.value

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

object TrueValue : Value {
    @Transient
    override val metaTable = DefaultBooleanMetaTable
    override fun toString() = "true"
}

object FalseValue : Value {
    @Transient
    override val metaTable = DefaultBooleanMetaTable
    override fun toString() = "false"
}

object DefaultBooleanMetaTable : MetaTable {
    override val type = "boolean"
    override val operations = listOf<ValueOperation>()
}