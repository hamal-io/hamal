package io.hamal.lib.kua

class Stack internal constructor(
    internal val state: State
) {
    fun isEmpty() = state.top() == 0
    fun isNotEmpty() = !isEmpty()
    fun size() = state.top()
    fun toBoolean(idx: Int) = state.toBoolean(idx)
    fun pushBoolean(value: Boolean) = state.pushBoolean(value)
}
