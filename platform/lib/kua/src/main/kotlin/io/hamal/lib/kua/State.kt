package io.hamal.lib.kua

import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import java.math.BigDecimal
import kotlin.reflect.KClass

@JvmInline
value class StackTop(val value: Int)

@JvmInline
value class TableLength(val value: Int)

interface State {
    fun absIndex(idx: Int): Int

    fun anyGet(idx: Int): KuaAny
    fun anyPush(value: KuaAny): StackTop

    fun booleanPush(value: KuaBoolean): StackTop
    fun booleanGet(idx: Int): KuaBoolean

    fun decimalPush(value: KuaDecimal): StackTop
    fun decimalGet(idx: Int): KuaDecimal

    fun errorPush(error: KuaError): StackTop
    fun errorGet(idx: Int): KuaError

    fun functionPush(value: KuaFunction<*, *, *, *>): StackTop

    fun nilPush(): StackTop
    fun numberGet(idx: Int): KuaNumber
    fun numberPush(value: KuaNumber): StackTop

    fun stringGet(idx: Int): KuaString
    fun stringPush(value: KuaString): StackTop

    fun tableAppend(idx: Int): TableLength
    fun tableCreate(arrayCount: Int, recordCount: Int): KuaTable
    fun tableFieldSet(idx: Int, key: KuaString): TableLength
    fun tableFieldGet(idx: Int, key: KuaString): StackTop
    fun tableGet(idx: Int): KuaTable
    fun tableLength(idx: Int): TableLength
    fun tablePush(proxy: KuaTable): StackTop
    fun tableRawSet(idx: Int): TableLength
    fun tableRawSetIdx(stackIdx: Int, tableIdx: Int): TableLength
    fun tableRawGet(idx: Int): KClass<out KuaType>
    fun tableRawGetIdx(stackIdx: Int, tableIdx: Int): KClass<out KuaType>

    fun topGet(): StackTop
    fun topPop(len: Int): StackTop
    fun topPush(idx: Int): StackTop
    fun topSet(idx: Int)

    fun type(idx: Int): KClass<out KuaType>


    /// OLD STUFF TO BE REPLACED

    fun setGlobal(name: String, value: KuaFunction<*, *, *, *>)
    fun setGlobal(name: String, value: KuaTable)

    fun getGlobalKuaTableMap(name: String): KuaTable
    fun unsetGlobal(name: String)


    fun load(code: String) // FIXME add return value
}

interface CloseableState : State, AutoCloseable

class CloseableStateImpl(private val native: Native = Native()) : CloseableState {

    override fun absIndex(idx: Int): Int = native.absIndex(idx)

    override fun anyGet(idx: Int): KuaAny {
        return when (val type = type(idx)) {
            KuaBoolean::class -> KuaAny(booleanGet(idx))
            KuaDecimal::class -> KuaAny(decimalGet(idx))
            KuaError::class -> KuaAny(errorGet(idx))
            KuaNil::class -> KuaAny(KuaNil)
            KuaNumber::class -> KuaAny(numberGet(idx))
            KuaString::class -> KuaAny(stringGet(idx))
            KuaTable::class -> KuaAny(tableGet(idx))
            else -> TODO("$type not supported yet")
        }
    }

    override fun anyPush(value: KuaAny): StackTop {
        return when (val underlying = value.value) {
            is KuaBoolean -> booleanPush(underlying)
            is KuaDecimal -> decimalPush(underlying)
            is KuaError -> errorPush(underlying)
            is KuaFunction<*, *, *, *> -> functionPush(underlying)
            is KuaNil -> nilPush()
            is KuaNumber -> numberPush(underlying)
            is KuaString -> stringPush(underlying)
            is KuaTable -> tablePush(underlying)
            else -> TODO("${underlying.javaClass} not supported yet")
        }
    }


    override fun booleanPush(value: KuaBoolean): StackTop = StackTop(native.booleanPush(value.value))
    override fun booleanGet(idx: Int) = KuaBoolean.of(native.booleanGet(idx))

    override fun decimalPush(value: KuaDecimal): StackTop = StackTop(
        native.decimalPush(value.toBigDecimal().toString())
    )

    override fun decimalGet(idx: Int): KuaDecimal = KuaDecimal(BigDecimal(native.decimalGet(idx)))

    override fun errorPush(error: KuaError) = StackTop(native.errorPush(error.value))
    override fun errorGet(idx: Int): KuaError = KuaError(native.errorGet(idx))

    override fun functionPush(value: KuaFunction<*, *, *, *>) = StackTop(native.functionPush(value))

    override fun nilPush() = StackTop(native.nilPush())
    override fun numberGet(idx: Int) = KuaNumber(native.numberGet(idx))
    override fun numberPush(value: KuaNumber) = StackTop(native.numberPush(value.value))

    override fun stringGet(idx: Int) = KuaString(native.stringGet(idx))
    override fun stringPush(value: KuaString) = StackTop(native.stringPush(value.value))


    override fun tableAppend(idx: Int) = TableLength(native.tableAppend(idx))
    override fun tableCreate(arrayCount: Int, recordCount: Int): KuaTable {
        return KuaTable(
            index = native.tableCreate(arrayCount, recordCount),
            state = this
        )
    }

    override fun tableFieldGet(idx: Int, key: KuaString) = StackTop(native.tableFieldGet(idx, key.value))
    override fun tableFieldSet(idx: Int, key: KuaString) = TableLength(native.tableFieldSet(idx, key.value))
    override fun tableGet(idx: Int) = KuaTable(native.tableGet(native.absIndex(idx)), this)
    override fun tableLength(idx: Int) = TableLength(native.tableLength(idx))
    override fun tablePush(proxy: KuaTable) = StackTop(native.topPush(proxy.index))
    override fun tableRawSet(idx: Int) = TableLength(native.tableRawSet(idx))
    override fun tableRawSetIdx(stackIdx: Int, tableIdx: Int) = TableLength(native.tableRawSetIdx(stackIdx, tableIdx))
    override fun tableRawGet(idx: Int) = luaToType(native.tableRawGet(idx))
    override fun tableRawGetIdx(stackIdx: Int, tableIdx: Int) = luaToType(native.tableRawGetIdx(stackIdx, tableIdx))

    override fun topGet(): StackTop = StackTop(native.topGet())
    override fun topPop(len: Int) = StackTop(native.topPop(len))
    override fun topPush(idx: Int): StackTop = StackTop(native.topPush(idx))
    override fun topSet(idx: Int) = native.topSet(idx)

    override fun type(idx: Int) = luaToType(native.type(idx))

    // FIXME TO BE REPLACED

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
        return tableGet(native.topGet())
    }

    override fun unsetGlobal(name: String) {
        native.nilPush()
        native.globalSet(name)
    }


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
    6 -> KuaFunction::class
    10 -> KuaError::class
    11 -> KuaDecimal::class
    else -> TODO("$value not implemented yet")
}

fun <T : State> T.tableCreate(vararg pairs: Pair<KuaString, KuaType>): KuaTable = tableCreate(pairs.toMap())


fun <T : State> T.tableCreate(data: Map<KuaString, KuaType>): KuaTable {
    return tableCreate(0, data.size).also { map ->
        data.forEach { (key, value) ->
            map[key] = value
        }
    }
}

fun <T : State> T.tableCreate(data: List<KuaType>): KuaTable {
    return tableCreate(data.size, 0).also { array ->
        data.forEach(array::append)
    }
}