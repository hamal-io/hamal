package io.hamal.lib.domain.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: Value, val value: Value)


@Serializable
@SerialName("Table")
class TableValue(
    val entries: List<TableEntry>,
    @Transient
    override val metaTable: MetaTable = DefaultTableMetaTable
) : Value

object DefaultTableMetaTable : MetaTable {
    override val type = "table"
    override val operations: List<ValueOperation> = listOf()
}