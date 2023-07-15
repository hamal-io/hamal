package io.hamal.lib.kua

abstract class KuaFunc {
    abstract fun invokedByLua(state: LuaState): Int
//    fun invoke(): Int
}

class TestFunc : KuaFunc() {
    override fun invokedByLua(state: LuaState): Int {
        println("Hamal Rockz")
        println(state)
        return 0
    }
}