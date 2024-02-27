package io.hamal.lib.kua

import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import java.math.BigDecimal
import kotlin.reflect.KClass

@JvmInline
value class StackTop(val value: Int)

interface State {
    fun absIndex(idx: Int): Int

    fun booleanPush(value: KuaBoolean): StackTop
    fun booleanGet(idx: Int): KuaBoolean

    fun decimalPush(value: KuaDecimal): StackTop
    fun decimalGet(idx: Int): KuaDecimal

    fun errorPush(error: KuaError): StackTop
    fun errorGet(idx: Int): KuaError

    fun topGet(): StackTop
    fun topPop(len: Int): StackTop
    fun topPush(idx: Int): StackTop
    fun topSet(idx: Int)

    fun type(idx: Int): KClass<out KuaType>

    /// OLD STUFF TO BE REPLACED


    fun pushNil(): StackTop
    fun pushAny(value: KuaAny): StackTop
    fun getAny(idx: Int): KuaAny


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

interface CloseableState : State, AutoCloseable

class CloseableStateImpl(private val native: Native = Native()) : CloseableState {

    override fun absIndex(idx: Int): Int = native.absIndex(idx)

    override fun booleanPush(value: KuaBoolean): StackTop = StackTop(native.booleanPush(value.value))
    override fun booleanGet(idx: Int): KuaBoolean = KuaBoolean.of(native.booleanGet(idx))

    override fun decimalPush(value: KuaDecimal): StackTop =
        StackTop(native.decimalPush(value.toBigDecimal().toString()))

    override fun decimalGet(idx: Int): KuaDecimal = KuaDecimal(BigDecimal(native.decimalGet(idx)))

    override fun errorPush(error: KuaError) = StackTop(native.errorPush(error.value))

    override fun errorGet(idx: Int): KuaError = KuaError(native.errorGet(idx))

    override fun topGet(): StackTop = StackTop(native.topGet())
    override fun topSet(idx: Int) = native.topSet(idx)


    // FIXME TO BE REPLACED

    override fun topPop(len: Int) = StackTop(native.topPop(len))

//    override fun isEmpty() = native.topGet() == 0
//    override fun isNotEmpty() = !isEmpty()


    override fun topPush(idx: Int): StackTop = StackTop(native.topPush(idx))

    override fun type(idx: Int) = luaToType(native.type(idx))

    override fun pushNil() = StackTop(native.nilPush())

    override fun pushAny(value: KuaAny): StackTop {
        return when (val underlying = value.value) {
            is KuaBoolean -> booleanPush(underlying)
            is KuaTable -> pushTable(underlying)
            is KuaTable -> pushTable(underlying)
            is KuaNumber -> pushNumber(underlying)
            is KuaString -> pushString(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }

    override fun getAny(idx: Int): KuaAny {
        return when (val type = type(idx)) {
            KuaBoolean::class -> KuaAny(booleanGet(idx))
            KuaDecimal::class -> KuaAny(decimalGet(idx))
            KuaNumber::class -> KuaAny(getNumberType(idx))
            KuaString::class -> KuaAny(getStringType(idx))
            KuaTable::class -> KuaAny(getTableMap(idx))
            KuaTable::class -> KuaAny(getTableArray(idx))
            KuaTable::class -> KuaAny(getTable(idx))
            else -> TODO("$type not supported yet")
        }
    }


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
        return getTableMap(native.topGet())
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
        native.stringLoad(code)
        native.functionCall(0, 0)
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
    10 -> KuaError::class
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