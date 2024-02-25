package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import kotlin.collections.Map.Entry

class KuaTableEntryIterator<KEY : KuaType, TYPE : KuaType>(
    index: Int,
    val state: State,
    val keyExtractor: (State, Int) -> KEY,
    val valueExtractor: (State, Int) -> TYPE
) : Iterator<Entry<KEY, TYPE>> {

    private val index = state.absIndex(index)
    private var hasNext: Boolean = true
    private var nextTableEntry: Entry<KEY, TYPE>? = null

    init {
        state.pushNil()
    }

    override fun hasNext(): Boolean {
        hasNext = state.native.tableNext(index)
        return if (hasNext) {
            nextTableEntry = object : Entry<KEY, TYPE> {
                override val key = keyExtractor(state, state.absIndex(-2))
                override val value = valueExtractor(state, state.absIndex(-1))
            }
            state.native.pop(1)
            true
        } else {
            nextTableEntry = null
            false
        }
    }

    override fun next(): Entry<KEY, TYPE> {
        return nextTableEntry ?: throw NoSuchElementException("Iterator exhausted")
    }
}
