package io.hamal.lib.kua.value

import io.hamal.lib.kua.Bridge

abstract class FuncValue : Value {
    override val type = Value.Type.Func

    abstract fun invokedByLua(state: Bridge): Int
//    fun invoke(): Int
}

data class NamedFuncValue(
    val name: String,
    val func: FuncValue
)

class TestFunc : FuncValue() {
    override fun invokedByLua(state: Bridge): Int {
        print("Hamal Rockz\n")
//        println(state.size())
//        println("INFO: " + state.toString(1))

        return 0
    }
}

