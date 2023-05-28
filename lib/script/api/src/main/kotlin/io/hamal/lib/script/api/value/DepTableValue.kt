package io.hamal.lib.script.api.value

typealias TableEntry = Pair<DepValue, DepValue>

open class DepTableValue : DepValue, Collection<TableEntry> {
    override val metaTable = DepMetaTableNotImplementedYet

    constructor(entries: Map<DepValue, DepValue>) {
        entries.forEach { store[it.key] = it.value }
    }

    constructor(vararg entries: Pair<DepValue, DepValue>) {
        entries.forEach { store[it.first] = it.second }
    }


//    data class TableEntry(val key: Value, val value: Value)


    private val store = mutableMapOf<DepValue, DepValue>()

    operator fun set(key: Int, value: DepValue) {
        this[DepNumberValue(key)] = value
    }

    operator fun set(key: DepValue, value: DepValue) {
        store[key] = value
    }

    operator fun get(key: Int): DepValue = store[DepNumberValue(key)] ?: DepNilValue
    operator fun get(key: DepValue): DepValue = store[key] ?: DepNilValue

    fun remove(key: Int) {
        remove(DepNumberValue(key))
    }

    fun remove(key: DepValue) {
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

        other as DepTableValue

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