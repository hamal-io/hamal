package io.hamal.lib.kua

class Stack internal constructor(
    val state: State
) {
    fun isEmpty() = state.top() == 0
    fun isNotEmpty() = !isEmpty()
    fun size() = state.top()
}
