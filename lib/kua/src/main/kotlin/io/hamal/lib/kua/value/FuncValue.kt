package io.hamal.lib.kua.value

import io.hamal.lib.kua.LuaState

abstract class FuncValue {
    abstract fun invokedByLua(state: LuaState): Int
//    fun invoke(): Int
}

data class NamedFuncValue(
    val name: String,
    val func: FuncValue
)

class TestFunc : FuncValue() {
    override fun invokedByLua(state: LuaState): Int {
//        println("Hamal Rockz")
//        println(state.size())
        println("INFO: " + state.toString(1))
        return 0
    }
}

