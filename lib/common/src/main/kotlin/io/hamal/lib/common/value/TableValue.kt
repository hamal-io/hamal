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
) : Value, Collection<TableEntry> {

    constructor(vararg pairs: Pair<Value, Value>) : this(listOf()) //FIXME
    constructor(map: Map<Value, Value>) : this(listOf()) //FIXME

    companion object {
        fun empty() = TableValue(listOf())
    }


    operator fun set(key: Int, value: Value) {
        this[NumberValue(key)] = value
    }

    operator fun set(key: Value, value: Value) {
//        store[key] = value
        TODO()
    }

    operator fun get(key: Int): Value = TODO() //store[DepNumberValue(key)] ?: DepNilValue
    operator fun get(key: Value): Value = TODO() // store[key] ?: DepNilValue

    fun remove(key: Int) {
        remove(NumberValue(key))
    }

    fun remove(key: Value) {
//        store.remove(key)
        TODO()
    }

    override val size: Int get() = TODO() //store.size

    override fun isEmpty() = TODO() // store.isEmpty()

    override fun iterator(): Iterator<TableEntry> {
//        return store.asIterable().map { entry -> TableEntry(entry.key, entry.value) }.iterator()
        TODO()
    }

    override fun containsAll(elements: Collection<TableEntry>) = elements.all(::contains)
    override fun contains(element: TableEntry) = TODO() // store.containsKey(element.first)
    override fun equals(other: Any?): Boolean {
        TODO()
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as TableValue
//
//        return store == other.store
    }

    override fun hashCode(): Int {
        TODO()
//        return store.hashCode()
    }

    override fun toString(): String {
        TODO()
//        val valueString = store.map { "(${it.key},${it.value})" }.joinToString(",")
//        return "{$valueString}"
    }
}

object DefaultTableMetaTable : MetaTable {
    override val type = "table"
    override val operators: List<ValueOperator> = listOf()
}
