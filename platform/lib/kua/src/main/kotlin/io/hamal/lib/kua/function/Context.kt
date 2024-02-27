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

    override fun booleanGet(idx: Int) = state.booleanGet(idx)
    override fun booleanPush(value: KuaBoolean) = state.booleanPush(value)

    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)
    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun numberGet(idx: Int) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun stringGet(idx: Int) = state.stringGet(idx)
    override fun stringPush(value: KuaString) = state.stringPush(value)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: Int) = state.topPop(len)
    override fun topPush(idx: Int) = state.topPush(idx)
    override fun topSet(idx: Int) = state.topSet(idx)


    // FIXME


    override fun type(idx: Int) = state.type(idx)
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: KuaAny) = state.pushAny(value)
    override fun getAny(idx: Int) = state.getAny(idx)

    override fun pushTable(proxy: KuaTable) = state.pushTable(proxy)

    override fun getTable(idx: Int): KuaTable = state.getTable(idx)
    override fun getTableArray(idx: Int): KuaTable = state.getTableArray(idx)
    override fun getTableMap(idx: Int): KuaTable = state.getTableMap(idx)

    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: KuaTable) = state.setGlobal(name, value)

    override fun getGlobalKuaTableMap(name: String): KuaTable = state.getGlobalKuaTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int): KuaTable = state.tableCreateArray(capacity)

    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)

    override fun load(code: String) = state.load(code)


    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        TODO()
//        return native.sandbox!!.ctx[clazz]
    }
}