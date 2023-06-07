package io.hamal.lib.common.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("IdentValue")
data class IdentValue(val value: String) : Value {
    @Transient
    override val metaTable = DefaultIdentMetaTable

}

object DefaultIdentMetaTable : MetaTable {
    override val type = "ident"
    override val operators: List<ValueOperator> = listOf()
}

