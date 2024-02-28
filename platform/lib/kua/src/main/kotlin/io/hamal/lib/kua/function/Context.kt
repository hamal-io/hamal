package io.hamal.lib.kua.function

import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun anyGet(idx: Int) = state.anyGet(idx)
    override fun anyPush(value: KuaAny) = state.anyPush(value)

    override fun booleanGet(idx: Int) = state.booleanGet(idx)
    override fun booleanPush(value: KuaBoolean) = state.booleanPush(value)

    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)
    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: Int) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun stringGet(idx: Int) = state.stringGet(idx)
    override fun stringPush(value: KuaString) = state.stringPush(value)

    override fun tableCreate(arrayCount: Int, recordCount: Int) = state.tableCreate(arrayCount, recordCount)
    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableFieldGet(idx: Int, key: KuaString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: Int, key: KuaString) = state.tableFieldSet(idx, key)
    override fun tableGet(idx: Int): KuaTable = state.tableGet(idx)
    override fun tableLength(idx: Int) = state.tableLength(idx)
    override fun tablePush(proxy: KuaTable): StackTop = state.tablePush(proxy)
    override fun tableRawSet(idx: Int) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: Int) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawGetIdx(stackIdx, tableIdx)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: Int) = state.topPop(len)
    override fun topPush(idx: Int) = state.topPush(idx)
    override fun topSet(idx: Int) = state.topSet(idx)

    override fun type(idx: Int) = state.type(idx)


    // FIXME

    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: KuaTable) = state.setGlobal(name, value)

    override fun getGlobalKuaTableMap(name: String): KuaTable = state.getGlobalKuaTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)

    override fun load(code: String) = state.load(code)

    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        TODO()
//        return native.sandbox!!.ctx[clazz]
    }


// FIXME add for easier access
//    fun tableCreate(vararg pairs: Pair<String, Any>): KuaTable = tableCreate(pairs.toMap())
//     fun tableCreate(data: Map<String, Any>): KuaTable
//     fun tableCreate(data: List<Any>): KuaTable

    fun tableCreate(vararg pairs: Pair<String, KuaType>): KuaTable = tableCreate(pairs.toMap())

    fun tableCreate(data: Map<String, KuaType>): KuaTable {
        return tableCreate(0, data.size).also { map ->
            data.forEach { (key, value) ->
                map[key] = value
            }
        }
    }
}