package io.hamal.lib.kua

class LuaState : AutoCloseable {

    external fun versionNumber(): Int
    external fun integerWidth(): Int

    external fun size(): Int
    external fun type(idx: Int): Int

    external fun pushBoolean(value: Boolean): Int
    external fun pushNil(): Int
    external fun pushNumber(value: Double): Int
    external fun pushString(value: String): Int
    external fun pushFunc(fn: KuaFunc)

    external fun toBoolean(idx: Int): Boolean
    external fun toNumber(idx: Int): Double
    external fun toString(idx: Int): String

    external fun loadString(code: String): Int
    external fun call(argCount: Int, returnCount: Int)

    private external fun init()

    init {
        init()
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "LuaState"
    }
}
