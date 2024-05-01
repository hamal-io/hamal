package io.hamal.lib.kua.value

import io.hamal.lib.kua.State
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.topPop
import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueNumber
import kotlin.collections.Map.Entry

class KuaTableIterator<KEY : Value, TYPE : Value>(
    index: ValueNumber,
    val state: State,
    val keyExtractor: (State, ValueNumber) -> KEY,
    val valueExtractor: (State, ValueNumber) -> TYPE
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
