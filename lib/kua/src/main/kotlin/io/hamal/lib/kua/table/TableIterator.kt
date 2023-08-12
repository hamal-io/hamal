package io.hamal.lib.kua.table

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.Value

class TableEntryIterator<KEY : Value, VALUE : Value>(
    index: Int,
    val state: State,
    val keyExtractor: (State, Int) -> KEY,
    val valueExtractor: (State, Int) -> VALUE
) : Iterator<Map.Entry<KEY, VALUE>> {

    private val index = state.absIndex(index)
    private var hasNext: Boolean = true
    private var nextTableEntry: Map.Entry<KEY, VALUE>? = null

    init {
        state.pushNil()
    }

    override fun hasNext(): Boolean {
        hasNext = state.native.tableNext(index)
        return if (hasNext) {
            nextTableEntry = object : Map.Entry<KEY, VALUE> {
                override val key = keyExtractor(state, -2)
                override val value = valueExtractor(state, -1)
            }
            state.native.pop(1)
            true
        } else {
            nextTableEntry = null
            false
        }
    }

    override fun next(): Map.Entry<KEY, VALUE> {
        return nextTableEntry ?: throw NoSuchElementException("Iterator exhausted")
    }
}