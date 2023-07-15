package io.hamal.lib.kua

abstract class KuaFunc {
    abstract fun invokedByLua(state: LuaState): Int
//    fun invoke(): Int
}

data class NamedKuaFunc(
    val name: String,
    val func: KuaFunc
)

class TestFunc : KuaFunc() {
    override fun invokedByLua(state: LuaState): Int {
//        println("Hamal Rockz")
//        println(state.size())
        println("INFO: " + state.toString(1))
        return 0
    }
}

