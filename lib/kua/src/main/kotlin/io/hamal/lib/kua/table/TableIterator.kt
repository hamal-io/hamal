package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.Value

data class TableEntry<KEY : Value, VALUE : Value>(val key: KEY, val value: VALUE)

class TableEntryIterator<KEY : Value, VALUE : Value>(
    index: Int,
    val state: State,
    val keyExtractor: (State, Int) -> KEY,
    val valueExtractor: (State, Int) -> VALUE
) : Iterator<TableEntry<KEY, VALUE>> {

    private val index = state.absIndex(index)
    private var hasNext: Boolean = true
    private var nextTableEntry: TableEntry<KEY, VALUE>? = null

    init {
        state.pushNil()
    }

    override fun hasNext(): Boolean {
        hasNext = state.native.tableNext(index)
        return if (hasNext) {
            val k = keyExtractor(state, -2)
            val v = valueExtractor(state, -1)
            state.native.pop(1)
            nextTableEntry = TableEntry(k, v)
            true
        } else {
            nextTableEntry = null
            false
        }
    }

    override fun next(): TableEntry<KEY, VALUE> {
        return nextTableEntry ?: throw NoSuchElementException("Iterator exhausted")
    }
}