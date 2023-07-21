package io.hamal.lib.kua.function

import io.hamal.lib.kua.State


class FunctionContext(
    val state: State
) {
    val stack = state.stack
}

interface FunctionContextFactory {
    fun create(state: State): FunctionContext
}