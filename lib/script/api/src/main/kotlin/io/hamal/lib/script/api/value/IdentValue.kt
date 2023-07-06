package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("IdentValue")
data class IdentValue(val value: String) : Value {
    @Transient
    override val metaTable = DefaultIdentValueMetaTable
}

object DefaultIdentValueMetaTable : MetaTable<IdentValue> {
    override val type = "ident"
    override val operators: List<ValueOperator> = listOf()
    override val props: Map<IdentValue, ValueProp<IdentValue>> = mapOf()
}

