package io.hamal.lib.domain.value

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
    override val type = "String"
    override val operations: List<ValueOperation> = listOf()
}
