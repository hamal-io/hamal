package io.hamal.lib.kua.value

import io.hamal.lib.kua.State

abstract class FuncValue {
    abstract fun invokedByLua(state: State): Int
//    fun invoke(): Int
}

data class NamedFuncValue(
    val name: String,
    val func: FuncValue
)

class TestFunc : FuncValue() {
    override fun invokedByLua(state: State): Int {
        print("Hamal Rockz\n")
//        println(state.size())
//        println("INFO: " + state.toString(1))

        return 0
    }
}

