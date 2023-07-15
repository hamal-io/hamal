package io.hamal.lib.kua

internal class LuaState : AutoCloseable {

    external fun versionNumber(): Int
    external fun integerWidth(): Int

    external fun size(): Int
    external fun type(idx: Int): Int
    external fun pushBoolean(value: Boolean): Int
    external fun toBoolean(idx: Int): Boolean

    external fun loadString(code: String): Int
    external fun call(argCount: Int, returnCount: Int)

    private external fun init()

    init {
        init()
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}
