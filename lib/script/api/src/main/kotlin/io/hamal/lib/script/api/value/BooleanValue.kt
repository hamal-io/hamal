package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

@Serializable
@SerialName("TrueValue")
object TrueValue : Value {
    @Transient
    override val metaTable = DefaultBooleanMetaTable
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseValue")
object FalseValue : Value {
    @Transient
    override val metaTable = DefaultBooleanMetaTable
    override fun toString() = "false"
}

object DefaultBooleanMetaTable : MetaTable {
    override val type = "boolean"
    override val operators = listOf<ValueOperator>()
}