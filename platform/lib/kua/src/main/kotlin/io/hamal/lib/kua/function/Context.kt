package io.hamal.lib.kua.function

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun anyGet(idx: Int) = state.anyGet(idx)
    override fun anyPush(value: KuaAny) = state.anyPush(value)

    override fun booleanGet(idx: Int) = state.booleanGet(idx)
    override fun codeLoad(code: KuaCode) = state.codeLoad(code)

    override fun booleanPush(value: KuaBoolean) = state.booleanPush(value)

    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)
    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: KuaString) = state.globalGet(key)
    override fun globalGetTable(key: KuaString) = state.globalGetTable(key)
    override fun globalSet(key: KuaString, value: KuaType) = state.globalSet(key, value)
    override fun globalUnset(key: KuaString) = state.globalUnset(key)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: Int) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun referenceAcquire() = state.referenceAcquire()
    override fun referencePush(reference: Reference) = state.referencePush(reference)
    override fun referenceRelease(reference: Reference) = state.referenceRelease(reference)

    override fun stringGet(idx: Int) = state.stringGet(idx)
    override fun stringPush(value: KuaString) = state.stringPush(value)

    override fun tableCreate(arrayCount: Int, recordCount: Int) = state.tableCreate(arrayCount, recordCount)
    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableFieldGet(idx: Int, key: KuaString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: Int, key: KuaString) = state.tableFieldSet(idx, key)
    override fun tableGet(idx: Int): KuaTable = state.tableGet(idx)
    override fun tableLength(idx: Int) = state.tableLength(idx)
    override fun tableHasNext(idx: Int) = state.tableHasNext(idx)
    override fun tableNext(idx: Int) = state.tableNext(idx)
    override fun tablePush(value: KuaTable): StackTop = state.tablePush(value)
    override fun tableRawSet(idx: Int) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: Int) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawGetIdx(stackIdx, tableIdx)
    override fun tableSubTableGet(idx: Int, key: KuaString) = state.tableSubTableGet(idx, key)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: Int) = state.topPop(len)
    override fun topPush(idx: Int) = state.topPush(idx)
    override fun topSet(idx: Int) = state.topSet(idx)

    override fun type(idx: Int) = state.type(idx)

    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return sandboxContextLocal.get()[clazz]
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