package io.hamal.lib.kua

import io.hamal.lib.kua.value.FuncValue


class LuaState : AutoCloseable {

    external fun luaVersionNumber(): Int
    external fun luaIntegerWidth(): Int

    external fun top(): Int
    external fun type(idx: Int): Int
    external fun setGlobal(key: String)

    external fun push(idx: Int): Int
    external fun pushBoolean(value: Boolean): Int
    external fun pushFuncValue(fn: FuncValue)
    external fun pushNil(): Int
    external fun pushNumber(value: Double): Int
    external fun pushString(value: String): Int

    external fun pop(total: Int): Int

    external fun toBoolean(idx: Int): Boolean
    external fun toNumber(idx: Int): Double
    external fun toString(idx: Int): String

    external fun createTable(arrayCount: Int, recordCount: Int): Int
    external fun getSubTable(idx: Int, key: String): Int
    external fun rawGet(idx: Int)
    external fun rawGetI(idx: Int, key: Int)
    external fun setField(idx: Int, key: String)

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
