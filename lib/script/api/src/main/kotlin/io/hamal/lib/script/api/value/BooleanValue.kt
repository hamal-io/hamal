package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

fun booleanOf(value: Boolean) = if (value) TrueValue else FalseValue

@Serializable
@SerialName("BooleanValue")
sealed class BooleanValue(
    val boolean: Boolean,
    @Transient
    override val metaTable: MetaTable<*> = DefaultTableValueMetaTable
) : Value

@Serializable
@SerialName("TrueValue")
object TrueValue : BooleanValue(
    true,
    DefaultBooleanMetaTable
) {
    override fun toString() = "true"
}

@Serializable
@SerialName("FalseValue")
object FalseValue : BooleanValue(
    false,
    DefaultTableValueMetaTable
) {
    override fun toString() = "false"
}

object DefaultBooleanMetaTable : MetaTable<BooleanValue> {
    override val type = "boolean"
    override val operators = listOf<ValueOperator>()
    override val props: Map<IdentValue, ValueProp<BooleanValue>> = mapOf()
}