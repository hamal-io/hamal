package io.hamal.lib.kua.function

import io.hamal.lib.kua.State
import io.hamal.lib.kua.value.Value
import io.hamal.lib.kua.value.ValueType


class FunctionContext(
    val state: State
) : State {
    override val bridge = state.bridge

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun length() = state.length()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun type(idx: Int): ValueType = state.type(idx)
    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
    override fun <VALUE : Value> push(value: VALUE) = state.push(value)
}

interface FunctionContextFactory {
    fun create(state: State): FunctionContext
}