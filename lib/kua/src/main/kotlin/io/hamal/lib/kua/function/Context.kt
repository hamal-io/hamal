package io.hamal.lib.kua.function

import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.AnyType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.TableType
import kotlin.reflect.KClass


class FunctionContext(
    val state: State
) : State, SandboxContext {

    override val native = state.native
    override val top: StackTop get() = state.top

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: AnyType) = state.pushAny(value)
    override fun getAny(idx: Int) = state.getAny(idx)
    override fun getArrayType(idx: Int) = state.getArrayType(idx)

    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: ErrorType) = state.pushError(value)
    override fun pushFunction(value: FunctionType<*, *, *, *>) = state.pushFunction(value)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
    override fun pushTable(value: TableType) = state.pushTable(value)
    override fun pushTable(proxy: TableProxyMap) = state.pushTable(proxy)
    override fun pushTable(proxy: TableProxyArray) = state.pushTable(proxy)
    override fun getTable(idx: Int) = state.getTable(idx)
    override fun getTableMap(idx: Int) = state.getTableMap(idx)
    override fun getTableArray(idx: Int) = state.getTableArray(idx)

    override fun getMapType(idx: Int): MapType = state.getMapType(idx)


    override fun setGlobal(name: String, value: FunctionType<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableProxyMap) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableProxyArray) = state.setGlobal(name, value)
    override fun getGlobalTableMap(name: String): TableProxyMap = state.getGlobalTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)

    override fun load(code: String) = state.load(code)

    override fun <OBJ : Any> get(clazz: KClass<OBJ>): OBJ {
        return native.sandbox.ctx[clazz]
    }

}