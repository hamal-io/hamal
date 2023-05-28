package io.hamal.lib.domain.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
@SerialName("TableEntry")
data class TableEntry(val key: Value, val value: Value)

infix fun <KEY : Value, VALUE : Value> KEY.to(other: VALUE): TableEntry = TableEntry(this, other)

@Serializable
@SerialName("Table")
class TableValue(
    val entries: List<TableEntry>
) : Value {
    @Transient
    override val metaTable: MetaTable = DefaultTableMetaTable
}

object DefaultTableMetaTable : MetaTable {
    override val type = "Table"
    override val operations: List<ValueOperation> = listOf()
}