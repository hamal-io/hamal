package io.hamal.lib.kua

class LuaStack internal constructor(
    internal val state: LuaState
) {
    fun isEmpty() = state.size() == 0
    fun isNotEmpty() = !isEmpty()
    fun size() = state.size()
    fun toBoolean(idx: Int) = state.toBoolean(idx)
    fun pushBoolean(value: Boolean) = state.pushBoolean(value)
}
