package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("StringValue")
data class StringValue(val value: String) : KeyValue {
    @Transient
    override val metaTable = DefaultStringValueMetaTable

}

object DefaultStringValueMetaTable : MetaTable {
    override val type = "string"
    override val operators: List<ValueOperator> = listOf()
}

