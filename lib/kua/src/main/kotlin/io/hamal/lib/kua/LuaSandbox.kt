package io.hamal.lib.kua

import io.hamal.lib.kua.value.CodeValue

class LuaSandbox (internal val state: LuaState)  {
    internal val stack = LuaStack(state)

    fun runCode(code: CodeValue){
        state.loadString(code.value)
        state.call(0, 0)
    }
}