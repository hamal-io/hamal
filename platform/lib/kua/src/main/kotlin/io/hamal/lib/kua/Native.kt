package io.hamal.lib.kua

import io.hamal.lib.kua.type.KuaFunction

class Native : AutoCloseable {

    external fun luaVersionNumber(): Int
    external fun luaIntegerWidth(): Int
    external fun luaRegistryIndex(): Int

    external fun topGet(): Int
    external fun topSet(idx: Int)
    external fun topPush(idx: Int): Int
    external fun topPop(total: Int): Int

    external fun absIndex(idx: Int): Int
    external fun type(idx: Int): Int

    external fun globalGet(key: String)
    external fun globalGetTable(key: String)
    external fun globalSet(key: String)

    external fun booleanPush(value: Boolean): Int
    external fun booleanGet(idx: Int): Boolean

    external fun decimalPush(value: String): Int
    external fun decimalGet(idx: Int): String

    external fun errorPush(message: String): Int
    external fun errorGet(idx: Int): String

    external fun functionPush(value: KuaFunction<*, *, *, *>): Int
    external fun functionCall(argCount: Int, returnCount: Int)

    external fun nilPush(): Int

    external fun numberPush(value: Double): Int
    external fun numberGet(idx: Int): Double

    external fun stringPush(value: String): Int
    external fun stringLoad(code: String): Int
    external fun stringGet(idx: Int): String

    external fun tableAppend(idx: Int): Int
    external fun tableGet(idx: Int): Int
    external fun tableCreate(arrayCount: Int, recordCount: Int): Int
    external fun tableFieldSet(idx: Int, key: String): Int
    external fun tableFieldGet(idx: Int, key: String): Int
    external fun tableLength(idx: Int): Int
    external fun tableHasNext(idx: Int): Boolean
    external fun tableNext(idx: Int): Boolean
    external fun tableRawSet(idx: Int): Int
    external fun tableRawSetIdx(stackIdx: Int, tableIdx: Int): Int
    external fun tableRawGet(idx: Int): Int
    external fun tableRawGetIdx(stackIdx: Int, tableIdx: Int): Int
    external fun tableSubTableGet(idx: Int, key: String): Int

    private external fun initConnection()
    private external fun closeConnection()

    init {
        initConnection()
    }

    override fun close() {
        closeConnection()
    }

    override fun toString(): String {
        return "Native"
    }
}
