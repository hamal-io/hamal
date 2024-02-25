package io.hamal.lib.kua

import io.hamal.lib.kua.type.TableProxyArray
import io.hamal.lib.kua.type.TableProxyMap
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import kotlin.reflect.KClass

@JvmInline
value class StackTop(val value: Int)

interface State {
    //FIXME probably not a good idea to expose this internal - only for development / prototyping
    val native: Native
    val top: StackTop

    fun pop(len: Int): StackTop

    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun setTop(idx: Int)
    fun absIndex(idx: Int): Int
    fun pushTop(idx: Int): StackTop

    fun type(idx: Int): KClass<out KuaType>
    fun pushNil(): StackTop
    fun pushAny(value: KuaAny): StackTop
    fun getAny(idx: Int): KuaAny

    fun pushBoolean(value: Boolean): StackTop
    fun pushBoolean(value: KuaBoolean) = pushBoolean(value.value)
    fun getBoolean(idx: Int): Boolean
    fun getBooleanValue(idx: Int) = booleanOf(getBoolean(idx))
    fun pushError(value: KuaError): StackTop
    fun pushFunction(value: KuaFunction<*, *, *, *>): StackTop

    fun getNumber(idx: Int): Double
    fun getNumberType(idx: Int) = KuaNumber(getNumber(idx))
    fun pushNumber(value: Double): StackTop
    fun pushNumber(value: KuaNumber) = pushNumber(value.value)

    fun getString(idx: Int): String
    fun getStringType(idx: Int) = KuaString(getString(idx))
    fun pushString(value: String): StackTop
    fun pushString(value: KuaString) = pushString(value.value)

    fun pushTable(proxy: TableProxyMap): StackTop
    fun pushTable(proxy: TableProxyArray): StackTop

    fun getTableProxyArray(idx: Int): TableProxyArray
    fun getTableProxyMap(idx: Int): TableProxyMap

    fun getTableArray(idx: Int): KuaTable.Array
    fun getTableMap(idx: Int): KuaTable.Map

    fun setGlobal(name: String, value: KuaFunction<*, *, *, *>)
    fun setGlobal(name: String, value: TableProxyMap)
    fun getGlobalTableMap(name: String): TableProxyMap
    fun unsetGlobal(name: String)

    fun tableCreateMap(capacity: Int = 0): TableProxyMap
    fun tableCreateArray(capacity: Int = 0): TableProxyArray
    fun tableAppend(idx: Int): Int
    fun tableSetRaw(idx: Int): Int
    fun tableSetRawIdx(stackIdx: Int, tableIdx: Int): Int
    fun tableGetRaw(idx: Int): KClass<out KuaType>
    fun tableGetRawIdx(stackIdx: Int, tableIdx: Int): KClass<out KuaType>

    fun load(code: String) // FIXME add return value
}

class ClosableState(
    override val native: Native
) : State, AutoCloseable {
    override val top: StackTop get() = StackTop(native.top())
    override fun pop(len: Int) = StackTop(native.pop(len))

    override fun isEmpty() = native.top() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = native.setTop(idx)
    override fun absIndex(idx: Int) = native.absIndex(idx)

    override fun pushTop(idx: Int): StackTop = StackTop(native.pushTop(idx))

    override fun type(idx: Int) = luaToType(native.type(idx))

    override fun pushNil() = StackTop(native.pushNil())

    override fun pushAny(value: KuaAny): StackTop {
        return when (val underlying = value.value) {
            is KuaBoolean -> pushBoolean(underlying)
            is KuaTable.Array -> pushTable(toTableProxyArray(underlying))
            is KuaTable.Map -> pushTable(toTableProxyMap(underlying))
            is KuaNumber -> pushNumber(underlying)
            is KuaString -> pushString(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAny(idx: Int): KuaAny {
        return when (val type = type(idx)) {
            KuaBoolean::class -> KuaAny(getBooleanValue(idx))
            KuaDecimal::class -> KuaAny(native.toDecimal(idx))
            KuaNumber::class -> KuaAny(getNumberType(idx))
            KuaString::class -> KuaAny(getStringType(idx))
            KuaTable.Map::class -> KuaAny(getTableMap(idx))
            KuaTableType::class -> KuaAny(getTableMap(idx))
            else -> TODO("$type not supported yet")
        }
    }

    override fun pushBoolean(value: Boolean): StackTop = StackTop(native.pushBoolean(value))
    override fun getBoolean(idx: Int): Boolean = native.toBoolean(idx)
    override fun pushError(value: KuaError) = StackTop(native.pushError(value.value))
    override fun pushFunction(value: KuaFunction<*, *, *, *>) = StackTop(native.pushFunction(value))

    override fun getNumber(idx: Int) = native.toNumber(idx)
    override fun pushNumber(value: Double) = StackTop(native.pushNumber(value))
    override fun getString(idx: Int) = native.toString(idx)
    override fun pushString(value: String) = StackTop(native.pushString(value))

    override fun pushTable(proxy: TableProxyMap) = StackTop(native.pushTop(proxy.index))
    override fun pushTable(proxy: TableProxyArray) = StackTop(native.pushTop(proxy.index))

    override fun getTableProxyArray(idx: Int) = TableProxyArray(absIndex(idx), this)
    override fun getTableProxyMap(idx: Int): TableProxyMap = TableProxyMap(absIndex(idx), this)

    override fun getTableArray(idx: Int): KuaTable.Array {
        val ref = TableProxyArray(absIndex(idx), this)
        return toKuaTableArray(ref)
    }

    override fun getTableMap(idx: Int): KuaTable.Map {
        val ref = TableProxyMap(absIndex(idx), this)
        return toKuaTableMap(ref)
    }

    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) {
        native.pushFunction(value)
        native.setGlobal(name)
    }

    override fun setGlobal(name: String, value: TableProxyMap) {
        native.pushTop(value.index)
        native.setGlobal(name)
    }

    override fun getGlobalTableMap(name: String): TableProxyMap {
        native.getGlobal(name)
        return getTableProxyMap(top.value)
    }

    override fun unsetGlobal(name: String) {
        native.pushNil()
        native.setGlobal(name)
    }

    override fun tableCreateMap(capacity: Int): TableProxyMap {
        return TableProxyMap(
            index = native.tableCreate(0, capacity),
            state = this
        )
    }

    override fun tableCreateArray(capacity: Int): TableProxyArray {
        return TableProxyArray(
            index = native.tableCreate(0, capacity),
            state = this
        )
    }

    override fun tableAppend(idx: Int) = native.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = native.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = native.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = luaToType(native.tableGetRaw(idx))
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = luaToType(native.tableGetRawIdx(stackIdx, tableIdx))

    override fun load(code: String) {
        native.load(code)
    }

    override fun close() {
        native.close()
    }
}

private fun luaToType(value: Int) = when (value) {
    0 -> KuaNil::class
    1 -> KuaBoolean::class
    3 -> KuaNumber::class
    4 -> KuaString::class
    5 -> KuaTableType::class
    11 -> KuaDecimal::class
    else -> TODO("$value not implemented yet")
}
