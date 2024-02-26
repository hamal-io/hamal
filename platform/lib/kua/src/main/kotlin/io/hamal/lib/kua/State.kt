package io.hamal.lib.kua

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

    fun pushTable(proxy: KuaTable): StackTop

    fun getTable(idx: Int): KuaTable
    fun getTableArray(idx: Int): KuaTable
    fun getTableMap(idx: Int): KuaTable

    fun setGlobal(name: String, value: KuaFunction<*, *, *, *>)
    fun setGlobal(name: String, value: KuaTable)

    fun getGlobalKuaTableMap(name: String): KuaTable
    fun unsetGlobal(name: String)

    fun tableCreateMap(capacity: Int = 0): KuaTable
    fun tableCreateArray(capacity: Int = 0): KuaTable
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
    override val top: StackTop get() = StackTop(native.topGet())
    override fun pop(len: Int) = StackTop(native.topPop(len))

    override fun isEmpty() = native.topGet() == 0
    override fun isNotEmpty() = !isEmpty()
    override fun setTop(idx: Int) = native.topSet(idx)
    override fun absIndex(idx: Int) = native.absIndex(idx)

    override fun pushTop(idx: Int): StackTop = StackTop(native.topPush(idx))

    override fun type(idx: Int) = luaToType(native.type(idx))

    override fun pushNil() = StackTop(native.nilPush())

    override fun pushAny(value: KuaAny): StackTop {
        return when (val underlying = value.value) {
            is KuaBoolean -> pushBoolean(underlying)
            is KuaTable -> pushTable(underlying)
            is KuaTable -> pushTable(underlying)
            is KuaNumber -> pushNumber(underlying)
            is KuaString -> pushString(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAny(idx: Int): KuaAny {
        return when (val type = type(idx)) {
            KuaBoolean::class -> KuaAny(getBooleanValue(idx))
            KuaDecimal::class -> KuaAny(native.decimalGet(idx))
            KuaNumber::class -> KuaAny(getNumberType(idx))
            KuaString::class -> KuaAny(getStringType(idx))
            KuaTable::class -> KuaAny(getTableMap(idx))
            KuaTable::class -> KuaAny(getTableArray(idx))
            KuaTable::class -> KuaAny(getTable(idx))
            else -> TODO("$type not supported yet")
        }
    }

    override fun pushBoolean(value: Boolean): StackTop = StackTop(native.booleanPush(value))
    override fun getBoolean(idx: Int): Boolean = native.booleanGet(idx)
    override fun pushError(value: KuaError) = StackTop(native.errorPush(value.value))
    override fun pushFunction(value: KuaFunction<*, *, *, *>) = StackTop(native.functionPush(value))

    override fun getNumber(idx: Int) = native.numberGet(idx)
    override fun pushNumber(value: Double) = StackTop(native.numberPush(value))
    override fun getString(idx: Int) = native.stringGet(idx)
    override fun pushString(value: String) = StackTop(native.stringPush(value))

    override fun pushTable(proxy: KuaTable) = StackTop(native.topPush(proxy.index))

    override fun getTable(idx: Int): KuaTable {
        return if (native.tableGetLength(idx) == 0) {
            getTableMap(idx)
        } else {
            getTableArray(idx)
        }
    }

    override fun getTableArray(idx: Int) = KuaTable(absIndex(idx), this)
    override fun getTableMap(idx: Int): KuaTable = KuaTable(absIndex(idx), this)

    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) {
        native.functionPush(value)
        native.globalSet(name)
    }

    override fun setGlobal(name: String, value: KuaTable) {
        native.topPush(value.index)
        native.globalSet(name)
    }

    override fun getGlobalKuaTableMap(name: String): KuaTable {
        native.globalGet(name)
        return getTableMap(top.value)
    }

    override fun unsetGlobal(name: String) {
        native.nilPush()
        native.globalSet(name)
    }

    override fun tableCreateMap(capacity: Int): KuaTable {
        return KuaTable(
            index = native.tableCreate(0, capacity),
            state = this
        )
    }

    override fun tableCreateArray(capacity: Int): KuaTable {
        return KuaTable(
            index = native.tableCreate(capacity, 0),
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
    5 -> KuaTable::class
    11 -> KuaDecimal::class
    else -> TODO("$value not implemented yet")
}

fun <T : State> T.createTable(vararg pairs: Pair<String, KuaType>): KuaTable = createTable(pairs.toMap())


fun <T : State> T.createTable(data: Map<String, KuaType>): KuaTable {
    return tableCreateMap(data.size).also { map ->
        data.forEach { (key, value) ->
            map[key] = value
        }
    }
}

fun <T : State> T.createTable(data: List<KuaType>): KuaTable {
    return tableCreateArray(data.size).also { array ->
        data.forEach(array::append)
    }
}