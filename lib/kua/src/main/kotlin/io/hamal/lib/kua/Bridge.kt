package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.value.ErrorValue


class Bridge : AutoCloseable {

    external fun luaVersionNumber(): Int
    external fun luaIntegerWidth(): Int
    external fun luaRegistryIndex(): Int

    external fun top(): Int
    external fun setTop(idx: Int)
    external fun pushTop(idx: Int): Int
    external fun absIndex(idx: Int): Int
    external fun type(idx: Int): Int
    external fun setGlobal(key: String)
    external fun getGlobal(key: String)

    external fun pushBoolean(value: Boolean): Int
    external fun pushFunctionValue(value: FunctionValue<*, *, *, *>): Int
    external fun pushError(message: String): Int
    external fun pushNil(): Int
    external fun pushNumber(value: Double): Int
    external fun pushString(value: String): Int

    external fun pop(total: Int): Int

    external fun toBoolean(idx: Int): Boolean
    external fun toError(idx: Int): ErrorValue
    external fun toNumber(idx: Int): Double
    external fun toString(idx: Int): String

    external fun tableCreate(arrayCount: Int, recordCount: Int): Int
    external fun tabletSetField(idx: Int, key: String): Int
    external fun tableGetField(idx: Int, key: String): Int
    external fun tableGetLength(idx: Int): Int

    external fun tableAppend(idx: Int): Int
    external fun tableSetRaw(idx: Int): Int
    external fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): Int
    external fun tableGetRaw(idx: Int): Int
    external fun tableGetRawIdx(stackIdx: Int, tableIdx: Int): Int
    external fun tableNext(idx: Int): Boolean
    external fun tableGetSub(idx: Int, key: String): Int

    external fun loadString(code: String): Int
    external fun call(argCount: Int, returnCount: Int)

    private external fun initConnection()
    private external fun closeConnection()

    init {
        initConnection()
    }

    override fun close() {
        closeConnection()
    }

    override fun toString(): String {
        return "State"
    }
}
