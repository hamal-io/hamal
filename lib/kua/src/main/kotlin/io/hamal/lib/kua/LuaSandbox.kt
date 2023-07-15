package io.hamal.lib.kua

class LuaSandbox internal constructor(internal val state: LuaState)  {
    val stack = LuaStack(state)
}