package io.hamal.lib.common.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("String")
data class StringValue(val value: String) : KeyValue {
    @Transient
    override val metaTable = DefaultStringMetaTable

}

object DefaultStringMetaTable : MetaTable {
    override val type = "string"
    override val operators: List<ValueOperator> = listOf()
}

