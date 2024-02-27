package io.hamal.lib.kua.function

import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.type.*
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)
    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)

    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)
    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)

    override fun topGet(): StackTop = state.topGet()

    // FIXME

    override fun pop(len: Int) = state.pop(len)

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: KuaAny) = state.pushAny(value)
    override fun getAny(idx: Int) = state.getAny(idx)

    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: KuaError) = state.pushError(value)
    override fun pushFunction(value: KuaFunction<*, *, *, *>) = state.pushFunction(value)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
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