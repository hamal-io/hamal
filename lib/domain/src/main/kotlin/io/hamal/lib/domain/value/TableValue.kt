package io.hamal.lib.domain.value

import io.hamal.lib.domain.vo.FuncInputs
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


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

fun main(){
    val t = TableValue(listOf(
        TableEntry(StringValue("hello"), StringValue("WOrld"))
    ))
    println( Json {}.encodeToString(Value.serializer(), t))


    println(Json{}.encodeToString(FuncInputs(listOf(
        TableEntry(StringValue("hello"), StringValue("WOrld"))
    ))))

    println(Json{}.decodeFromString(FuncInputs.serializer(), """
        [{"key":{"type":"String","value":"hello"},"value":{"type":"String","value":"WOrld"}}]
    """.trimIndent()))
}