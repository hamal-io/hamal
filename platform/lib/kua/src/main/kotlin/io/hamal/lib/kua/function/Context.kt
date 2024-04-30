package io.hamal.lib.kua.function

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.*
import io.hamal.lib.value.*
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override fun absIndex(idx: ValueNumber) = state.absIndex(idx)

    override fun get(idx: ValueNumber) = state.get(idx)
    override fun push(value: Value) = state.push(value)

    override fun booleanGet(idx: ValueNumber) = state.booleanGet(idx)
    override fun booleanPush(value: ValueBoolean) = state.booleanPush(value)

    override fun <T : Any> checkpoint(action: (State) -> T) = state.checkpoint(action)
    override fun codeLoad(code: ValueCode) = state.codeLoad(code)

    override fun decimalGet(idx: ValueNumber): ValueDecimal = state.decimalGet(idx)
    override fun decimalPush(value: ValueDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: ValueNumber): ValueError = state.errorGet(idx)
    override fun errorPush(error: ValueError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: ValueString) = state.globalGet(key)
    override fun globalGetTable(key: ValueString) = state.globalGetTable(key)
    override fun globalSet(key: ValueString, value: Value) = state.globalSet(key, value)
    override fun globalUnset(key: ValueString) = state.globalUnset(key)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: ValueNumber) = state.numberGet(idx)
    override fun numberPush(value: ValueNumber) = state.numberPush(value)

    override fun referenceAcquire() = state.referenceAcquire()
    override fun referencePush(reference: KuaReference) = state.referencePush(reference)
    override fun referenceRelease(reference: KuaReference) = state.referenceRelease(reference)

    override fun stringGet(idx: ValueNumber) = state.stringGet(idx)
    override fun stringPush(value: ValueString) = state.stringPush(value)

    override fun tableCreate(arrayCount: ValueNumber, recordCount: ValueNumber) =
        state.tableCreate(arrayCount, recordCount)

    override fun tableAppend(idx: ValueNumber) = state.tableAppend(idx)
    override fun tableFieldGet(idx: ValueNumber, key: ValueString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: ValueNumber, key: ValueString) = state.tableFieldSet(idx, key)
    override fun tableGet(idx: ValueNumber): KuaTable = state.tableGet(idx)
    override fun tableLength(idx: ValueNumber) = state.tableLength(idx)
    override fun tableNext(idx: ValueNumber) = state.tableNext(idx)
    override fun tablePush(value: KuaTable): StackTop = state.tablePush(value)
    override fun tableRawSet(idx: ValueNumber) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: ValueNumber) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) = state.tableRawGetIdx(stackIdx, tableIdx)
    override fun tableSubTableGet(idx: ValueNumber, key: ValueString) = state.tableSubTableGet(idx, key)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: ValueNumber) = state.topPop(len)
    override fun topPush(idx: ValueNumber) = state.topPush(idx)
    override fun topSet(idx: ValueNumber) = state.topSet(idx)

    override fun type(idx: ValueNumber) = state.type(idx)

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