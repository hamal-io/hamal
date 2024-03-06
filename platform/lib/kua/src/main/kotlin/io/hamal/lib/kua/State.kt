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
    fun absIndex(idx: KuaNumber): KuaNumber

    fun get(idx: KuaNumber): KuaType
    fun push(value: KuaType): StackTop

    fun booleanPush(value: KuaBoolean): StackTop
    fun booleanGet(idx: KuaNumber): KuaBoolean

    fun codeLoad(code: KuaCode)

    fun decimalPush(value: KuaDecimal): StackTop
    fun decimalGet(idx: KuaNumber): KuaDecimal

    fun errorPush(error: KuaError): StackTop
    fun errorGet(idx: KuaNumber): KuaError

    fun functionPush(value: KuaFunction<*, *, *, *>): StackTop

    fun globalSet(key: KuaString, value: KuaType)
    fun globalGet(key: KuaString): KuaType
    fun globalGetTable(key: KuaString): KuaTable
    fun globalUnset(key: KuaString)

    fun nilPush(): StackTop
    fun numberGet(idx: KuaNumber): KuaNumber
    fun numberPush(value: KuaNumber): StackTop

    fun referenceAcquire(): KuaReference
    fun referencePush(reference: KuaReference): KClass<out KuaType>
    fun referenceRelease(reference: KuaReference)

    fun stringGet(idx: KuaNumber): KuaString
    fun stringPush(value: KuaString): StackTop

    fun tableAppend(idx: KuaNumber): TableLength
    fun tableCreate(arrayCount: KuaNumber, recordCount: KuaNumber): KuaTable
    fun tableFieldSet(idx: KuaNumber, key: KuaString): TableLength
    fun tableFieldGet(idx: KuaNumber, key: KuaString): KClass<out KuaType>
    fun tableGet(idx: KuaNumber): KuaTable
    fun tableLength(idx: KuaNumber): TableLength
    fun tableNext(idx: KuaNumber): KuaBoolean
    fun tablePush(value: KuaTable): StackTop
    fun tableRawSet(idx: KuaNumber): TableLength
    fun tableRawSetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber): TableLength
    fun tableRawGet(idx: KuaNumber): KClass<out KuaType>
    fun tableRawGetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber): KClass<out KuaType>
    fun tableSubTableGet(idx: KuaNumber, key: KuaString): KClass<out KuaType>

    fun topGet(): StackTop
    fun topPop(len: KuaNumber): StackTop
    fun topPush(idx: KuaNumber): StackTop
    fun topSet(idx: KuaNumber)

    fun type(idx: KuaNumber): KClass<out KuaType>

    fun <T : Any> checkpoint(action: (State) -> T): T
}

open class StateImpl(val native: Native = Native()) : State {

    override fun absIndex(idx: KuaNumber) = KuaNumber(native.absIndex(idx.intValue))

    override fun get(idx: KuaNumber): KuaType {
        return when (val type = type(idx)) {
            KuaBoolean::class -> booleanGet(idx)
            KuaDecimal::class -> decimalGet(idx)
            KuaError::class -> errorGet(idx)
            KuaNil::class -> KuaNil
            KuaNumber::class -> numberGet(idx)
            KuaString::class -> stringGet(idx)
            KuaTable::class -> tableGet(idx)
            else -> TODO("$type not supported yet")
        }
    }

    override fun push(value: KuaType): StackTop {
        return when (value) {
            is KuaBoolean -> booleanPush(value)
            is KuaDecimal -> decimalPush(value)
            is KuaError -> errorPush(value)
            is KuaFunction<*, *, *, *> -> functionPush(value)
            is KuaNil -> nilPush()
            is KuaNumber -> numberPush(value)
            is KuaString -> stringPush(value)
            is KuaTable -> tablePush(value)
            else -> TODO("${value.javaClass} not supported yet")
        }
    }


    override fun booleanPush(value: KuaBoolean): StackTop = StackTop(native.booleanPush(value.booleanValue))
    override fun booleanGet(idx: KuaNumber) = KuaBoolean.of(native.booleanGet(idx.intValue))

    override fun <T : Any> checkpoint(action: (State) -> T): T {
        // FIXME 254 - add Checkpoint as a State implementation to make sure the stack below can not be altered
        val currentStackSize = native.topGet()
        try {
            return action(this)
        } finally {
            val afterStackSize = native.topGet()
            if (afterStackSize > currentStackSize) {
                native.topPop(afterStackSize - currentStackSize)
            }
        }
    }

    override fun codeLoad(code: KuaCode) {
        native.stringLoad(code.stringValue)
        native.functionCall(0, 0)
    }

    override fun decimalPush(value: KuaDecimal): StackTop = StackTop(
        native.decimalPush(value.toBigDecimal().toString())
    )

    override fun decimalGet(idx: KuaNumber): KuaDecimal = KuaDecimal(BigDecimal(native.decimalGet(idx.intValue)))

    override fun errorPush(error: KuaError) = StackTop(native.errorPush(error.value))
    override fun errorGet(idx: KuaNumber): KuaError = KuaError(native.errorGet(idx.intValue))

    override fun functionPush(value: KuaFunction<*, *, *, *>) = StackTop(native.functionPush(value))

    override fun globalGet(key: KuaString): KuaType {
        native.globalGet(key.stringValue)
        return get(-1)
    }

    override fun globalGetTable(key: KuaString): KuaTable {
        native.globalGetTable(key.stringValue)
        return tableGet(-1)
    }

    override fun globalSet(key: KuaString, value: KuaType) {
        push(value)
        native.globalSet(key.stringValue)
    }

    override fun globalUnset(key: KuaString) {
        native.nilPush()
        native.globalSet(key.stringValue)
    }

    override fun nilPush() = StackTop(native.nilPush())
    override fun numberGet(idx: KuaNumber) = KuaNumber(native.numberGet(idx.intValue))
    override fun numberPush(value: KuaNumber) = StackTop(native.numberPush(value.doubleValue))

    override fun referenceAcquire() = KuaReference(native.referenceAcquire())

    override fun referencePush(reference: KuaReference) = luaToType(
        native.referencePush(reference.value)
    )

    override fun referenceRelease(reference: KuaReference) {
        native.referenceRelease(reference.value)
    }

    override fun stringGet(idx: KuaNumber) = KuaString(native.stringGet(idx.intValue))
    override fun stringPush(value: KuaString) = StackTop(native.stringPush(value.stringValue))


    override fun tableAppend(idx: KuaNumber) = TableLength(native.tableAppend(idx.intValue))
    override fun tableCreate(arrayCount: KuaNumber, recordCount: KuaNumber): KuaTable {
        return KuaTable(
            index = KuaNumber(native.tableCreate(arrayCount.intValue, recordCount.intValue)),
            state = this
        )
    }

    override fun tableFieldGet(idx: KuaNumber, key: KuaString) =
        luaToType(native.tableFieldGet(idx.intValue, key.stringValue))

    override fun tableFieldSet(idx: KuaNumber, key: KuaString) =
        TableLength(native.tableFieldSet(idx.intValue, key.stringValue))

    override fun tableGet(idx: KuaNumber) = KuaTable(KuaNumber(native.tableGet(native.absIndex(idx.intValue))), this)
    override fun tableLength(idx: KuaNumber) = TableLength(native.tableLength(idx.intValue))
    override fun tableNext(idx: KuaNumber) = KuaBoolean.of(native.tableNext(idx.intValue))
    override fun tablePush(value: KuaTable) = StackTop(native.topPush(value.index.intValue))
    override fun tableRawSet(idx: KuaNumber) = TableLength(native.tableRawSet(idx.intValue))
    override fun tableRawSetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) =
        TableLength(native.tableRawSetIdx(stackIdx.intValue, tableIdx.intValue))

    override fun tableRawGet(idx: KuaNumber) = luaToType(native.tableRawGet(idx.intValue))
    override fun tableRawGetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) =
        luaToType(native.tableRawGetIdx(stackIdx.intValue, tableIdx.intValue))

    override fun tableSubTableGet(idx: KuaNumber, key: KuaString) =
        luaToType(native.tableSubTableGet(idx.intValue, key.stringValue))

    override fun topGet(): StackTop = StackTop(native.topGet())
    override fun topPop(len: KuaNumber) = StackTop(native.topPop(len.intValue))
    override fun topPush(idx: KuaNumber): StackTop = StackTop(native.topPush(idx.intValue))
    override fun topSet(idx: KuaNumber) = native.topSet(idx.intValue)

    override fun type(idx: KuaNumber) = luaToType(native.type(idx.intValue))

}

fun State.absIndex(idx: Int) = absIndex(KuaNumber(idx))
fun State.get(idx: Int) = get(KuaNumber(idx))
fun State.booleanGet(idx: Int) = booleanGet(KuaNumber(idx))
fun State.decimalGet(idx: Int) = decimalGet(KuaNumber(idx))
fun State.errorGet(idx: Int) = errorGet(KuaNumber(idx))
fun State.numberGet(idx: Int) = numberGet(KuaNumber(idx))
fun State.stringGet(idx: Int) = stringGet(KuaNumber(idx))
fun State.tableAppend(idx: Int) = tableAppend(KuaNumber(idx))
fun State.tableCreate(arrayCount: Int, recordCount: Int) = tableCreate(KuaNumber(arrayCount), KuaNumber(recordCount))
fun State.tableFieldGet(idx: Int, key: String) = tableFieldGet(KuaNumber(idx), KuaString(key))
fun State.tableFieldGet(idx: Int, key: KuaString) = tableFieldGet(KuaNumber(idx), key)
fun State.tableFieldSet(idx: Int, key: KuaString) = tableFieldSet(KuaNumber(idx), key)
fun State.tableGet(idx: Int) = tableGet(KuaNumber(idx))
fun State.tableLength(idx: Int) = tableLength(KuaNumber(idx))
fun State.tableNext(idx: Int) = tableNext(KuaNumber(idx))
fun State.tableRawGetIdx(stackIdx: Int, tableIdx: Int) = tableRawGetIdx(KuaNumber(stackIdx), KuaNumber(tableIdx))
fun State.tableRawGet(idx: Int) = tableRawGet(KuaNumber(idx))
fun State.tableRawSet(idx: Int) = tableRawSet(KuaNumber(idx))
fun State.tableRawSetIdx(stackIdx: Int, tableIdx: Int) = tableRawSetIdx(KuaNumber(stackIdx), KuaNumber(tableIdx))
fun State.tableSubTableGet(idx: Int, key: String) = tableSubTableGet(KuaNumber(idx), KuaString(key))
fun State.topPush(idx: Int) = topPush(KuaNumber(idx))
fun State.topPop(idx: Int) = topPop(KuaNumber(idx))
fun State.topSet(idx: Int) = topSet(KuaNumber(idx))


fun State.type(idx: Int) = type(KuaNumber(idx))

interface CloseableState : State, AutoCloseable

class CloseableStateImpl(native: Native = Native()) : StateImpl(native), CloseableState {
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