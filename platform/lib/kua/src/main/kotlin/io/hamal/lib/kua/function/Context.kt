package io.hamal.lib.kua.function

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueBoolean
import io.hamal.lib.value.ValueDecimal
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override fun absIndex(idx: KuaNumber) = state.absIndex(idx)

    override fun get(idx: KuaNumber) = state.get(idx)
    override fun push(value: Value) = state.push(value)

    override fun booleanGet(idx: KuaNumber) = state.booleanGet(idx)
    override fun booleanPush(value: ValueBoolean) = state.booleanPush(value)

    override fun <T : Any> checkpoint(action: (State) -> T) = state.checkpoint(action)
    override fun codeLoad(code: KuaCode) = state.codeLoad(code)

    override fun decimalGet(idx: KuaNumber): ValueDecimal = state.decimalGet(idx)
    override fun decimalPush(value: ValueDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: KuaNumber): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: KuaString) = state.globalGet(key)
    override fun globalGetTable(key: KuaString) = state.globalGetTable(key)
    override fun globalSet(key: KuaString, value: Value) = state.globalSet(key, value)
    override fun globalUnset(key: KuaString) = state.globalUnset(key)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: KuaNumber) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun referenceAcquire() = state.referenceAcquire()
    override fun referencePush(reference: KuaReference) = state.referencePush(reference)
    override fun referenceRelease(reference: KuaReference) = state.referenceRelease(reference)

    override fun stringGet(idx: KuaNumber) = state.stringGet(idx)
    override fun stringPush(value: KuaString) = state.stringPush(value)

    override fun tableCreate(arrayCount: KuaNumber, recordCount: KuaNumber) = state.tableCreate(arrayCount, recordCount)
    override fun tableAppend(idx: KuaNumber) = state.tableAppend(idx)
    override fun tableFieldGet(idx: KuaNumber, key: KuaString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: KuaNumber, key: KuaString) = state.tableFieldSet(idx, key)
    override fun tableGet(idx: KuaNumber): KuaTable = state.tableGet(idx)
    override fun tableLength(idx: KuaNumber) = state.tableLength(idx)
    override fun tableNext(idx: KuaNumber) = state.tableNext(idx)
    override fun tablePush(value: KuaTable): StackTop = state.tablePush(value)
    override fun tableRawSet(idx: KuaNumber) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: KuaNumber) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) = state.tableRawGetIdx(stackIdx, tableIdx)
    override fun tableSubTableGet(idx: KuaNumber, key: KuaString) = state.tableSubTableGet(idx, key)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: KuaNumber) = state.topPop(len)
    override fun topPush(idx: KuaNumber) = state.topPush(idx)
    override fun topSet(idx: KuaNumber) = state.topSet(idx)

    override fun type(idx: KuaNumber) = state.type(idx)

    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return sandboxContextLocal.get()[clazz]
    }


// FIXME add for easier access
//    fun tableCreate(vararg pairs: Pair<String, Any>): KuaTable = tableCreate(pairs.toMap())
//     fun tableCreate(data: Map<String, Any>): KuaTable
//     fun tableCreate(data: List<Any>): KuaTable

    fun tableCreate(vararg pairs: Pair<String, Value>): KuaTable = tableCreate(pairs.toMap())

    fun tableCreate(data: Map<String, Value>): KuaTable {
        return tableCreate(0, data.size).also { map ->
            data.forEach { (key, value) ->
                map[key] = value
            }
        }
    }
}