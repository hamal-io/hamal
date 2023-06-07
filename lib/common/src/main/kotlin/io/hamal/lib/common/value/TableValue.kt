package io.hamal.lib.common.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
sealed interface KeyValue : Value

@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: Value, val value: Value)

@Serializable
@SerialName("TableValue")
data class TableValue(
    val entries: List<TableEntry>,
    @Transient
    override val metaTable: MetaTable = DefaultTableMetaTable
) : Value {
    companion object {
        fun empty() = TableValue(listOf())
    }
}

object DefaultTableMetaTable : MetaTable {
    override val type = "table"
    override val operators: List<ValueOperator> = listOf()
}
