package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import kotlin.collections.Map.Entry

class KuaTableForEach(
    val index: Int,
    val state: State
) {

    fun <T : Any> map(action: (key: KuaType, value: KuaType) -> T): List<T> {
        val result = mutableListOf<T>()
        state.nilPush()
        while (state.tableHasNext(index) == KuaTrue) {
            state.tableNext(index)
            val key = state.anyGet(-2)
            val value = state.anyGet(-1)
            result.add(action(key.value, value.value))
            state.topPop(1)
        }
        state.topPop(1)
        return result
    }

    fun forEach(action: (key: KuaType, value: KuaType) -> Unit) {
        state.nilPush()
        while (state.tableHasNext(index) == KuaTrue) {
            state.tableNext(index)
            val key = state.anyGet(-2)
            val value = state.anyGet(-1)
            action(key.value, value.value)
            state.topPop(1)
        }
        state.topPop(1)
    }
}


class KuaTableIterator<KEY : KuaType, TYPE : KuaType>(
    index: Int,
    val state: State,
    val keyExtractor: (State, Int) -> KEY,
    val valueExtractor: (State, Int) -> TYPE
) : Iterator<Entry<KEY, TYPE>> {

    private val index = state.absIndex(index)
//    private var hasNext: KuaBoolean = KuaTrue
//    private var nextTableEntry: Entry<KEY, TYPE>? = null

    init {
        state.nilPush()
    }

    override fun hasNext(): Boolean {
        return state.tableHasNext(index).booleanValue
    }

    var x = false

    override fun next(): Entry<KEY, TYPE> {
        if (!hasNext()) {
            throw NoSuchElementException("Iterator exhausted")
        }


        state.tableNext(index)
        return object : Entry<KEY, TYPE> {
            override val key = keyExtractor(state, state.absIndex(-2))
            override val value = valueExtractor(state, state.absIndex(-1))
        }
    }
}
