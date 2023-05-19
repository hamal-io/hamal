package io.hamal.lib.script.api.value

typealias TableEntry = Pair<Value, Value>

open class TableValue : Value, Collection<TableEntry> {
    override val metaTable = MetaTableNotImplementedYet

    constructor(entries: Map<Value, Value>) {
        entries.forEach { store[it.key] = it.value }
    }

    constructor(vararg entries: Pair<Value, Value>) {
        entries.forEach { store[it.first] = it.second }
    }


//    data class TableEntry(val key: Value, val value: Value)


    private val store = mutableMapOf<Value, Value>()

    operator fun set(key: Int, value: Value) {
        this[NumberValue(key)] = value
    }

    operator fun set(key: Value, value: Value) {
        store[key] = value
    }

    operator fun get(key: Int): Value = store[NumberValue(key)] ?: NilValue
    operator fun get(key: Value): Value = store[key] ?: NilValue

    fun remove(key: Int) {
        remove(NumberValue(key))
    }

    fun remove(key: Value) {
        store.remove(key)
    }

    override val size: Int get() = store.size

    override fun isEmpty() = store.isEmpty()

    override fun iterator(): Iterator<TableEntry> {
        return store.asIterable().map { entry -> TableEntry(entry.key, entry.value) }.iterator()
    }

    override fun containsAll(elements: Collection<TableEntry>) = elements.all(::contains)
    override fun contains(element: TableEntry) = store.containsKey(element.first)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TableValue

        return store == other.store
    }

    override fun hashCode(): Int {
        return store.hashCode()
    }

    override fun toString(): String {
        val valueString = store.map { "(${it.key},${it.value})" }.joinToString(",")
        return "{$valueString}"
    }

}