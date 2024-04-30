package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.topPop
import io.hamal.lib.value.Value
import kotlin.collections.Map.Entry

class KuaTableIterator<KEY : KuaType, TYPE : Value>(
    index: KuaNumber,
    val state: State,
    val keyExtractor: (State, KuaNumber) -> KEY,
    val valueExtractor: (State, KuaNumber) -> TYPE
) : Iterator<Entry<KEY, TYPE>> {

    private val index = state.absIndex(index)
    private var hasNext: Boolean = true
    private var nextTableEntry: Entry<KEY, TYPE>? = null

    init {
        state.nilPush()
    }

    override fun hasNext(): Boolean {
        hasNext = state.tableNext(index).booleanValue
        return if (hasNext) {
            nextTableEntry = object : Entry<KEY, TYPE> {
                override val key = keyExtractor(state, state.absIndex(-2))
                override val value = valueExtractor(state, state.absIndex(-1))
            }
            state.topPop(1)
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
