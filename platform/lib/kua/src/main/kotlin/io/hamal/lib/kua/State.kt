package io.hamal.lib.kua

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaReference
import io.hamal.lib.kua.value.KuaTable
import java.math.BigDecimal
import kotlin.reflect.KClass

@JvmInline
value class StackTop(val value: Int)

@JvmInline
value class TableLength(val value: Int)

interface State {
    fun absIndex(idx: ValueNumber): ValueNumber

    fun get(idx: ValueNumber): Value
    fun push(value: Value): StackTop

    fun booleanPush(value: ValueBoolean): StackTop
    fun booleanGet(idx: ValueNumber): ValueBoolean

    fun codeLoad(code: ValueCode)

    fun decimalPush(value: ValueDecimal): StackTop
    fun decimalGet(idx: ValueNumber): ValueDecimal

    fun errorPush(error: ValueError): StackTop
    fun errorGet(idx: ValueNumber): ValueError

    fun functionPush(value: KuaFunction<*, *, *, *>): StackTop

    fun globalSet(key: ValueString, value: Value)
    fun globalGet(key: ValueString): Value
    fun globalGetTable(key: ValueString): KuaTable
    fun globalUnset(key: ValueString)

    fun nilPush(): StackTop
    fun numberGet(idx: ValueNumber): ValueNumber
    fun numberPush(value: ValueNumber): StackTop

    fun referenceAcquire(): KuaReference
    fun referencePush(reference: KuaReference): KClass<out Value>
    fun referenceRelease(reference: KuaReference)

    fun stringGet(idx: ValueNumber): ValueString
    fun stringPush(value: ValueString): StackTop

    fun tableAppend(idx: ValueNumber): TableLength
    fun tableCreate(arrayCount: ValueNumber, recordCount: ValueNumber): KuaTable
    fun tableFieldSet(idx: ValueNumber, key: ValueString): TableLength
    fun tableFieldGet(idx: ValueNumber, key: ValueString): KClass<out Value>
    fun tableGet(idx: ValueNumber): KuaTable
    fun tableLength(idx: ValueNumber): TableLength
    fun tableNext(idx: ValueNumber): ValueBoolean
    fun tablePush(value: KuaTable): StackTop
    fun tableRawSet(idx: ValueNumber): TableLength
    fun tableRawSetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber): TableLength
    fun tableRawGet(idx: ValueNumber): KClass<out Value>
    fun tableRawGetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber): KClass<out Value>
    fun tableSubTableGet(idx: ValueNumber, key: ValueString): KClass<out Value>

    fun topGet(): StackTop
    fun topPop(len: ValueNumber): StackTop
    fun topPush(idx: ValueNumber): StackTop
    fun topSet(idx: ValueNumber)

    fun type(idx: ValueNumber): KClass<out Value>

    fun <T : Any> checkpoint(action: (State) -> T): T
}

open class StateImpl(val native: Native = Native()) : State {

    override fun absIndex(idx: ValueNumber) = ValueNumber(native.absIndex(idx.intValue))

    override fun get(idx: ValueNumber): Value {
        return when (val type = type(idx)) {
            ValueBoolean::class -> booleanGet(idx)
            ValueDecimal::class -> decimalGet(idx)
            ValueError::class -> errorGet(idx)
            ValueNil::class -> ValueNil
            ValueNumber::class -> numberGet(idx)
            ValueString::class -> stringGet(idx)
            KuaTable::class -> tableGet(idx)
            else -> TODO("$type not supported yet")
        }
    }

    override fun push(value: Value): StackTop {
        return when (value) {
            is ValueBoolean -> booleanPush(value)
            is ValueDecimal -> decimalPush(value)
            is ValueError -> errorPush(value)
            is KuaFunction<*, *, *, *> -> functionPush(value)
            is ValueNil -> nilPush()
            is ValueNumber -> numberPush(value)
            is ValueString -> stringPush(value)
            is KuaTable -> tablePush(value)
            else -> TODO("${value.javaClass} not supported yet")
        }
    }


    override fun booleanPush(value: ValueBoolean): StackTop = StackTop(native.booleanPush(value.booleanValue))
    override fun booleanGet(idx: ValueNumber) = ValueBoolean(native.booleanGet(idx.intValue))

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

    override fun codeLoad(code: ValueCode) {
        native.stringLoad(code.stringValue)
        native.functionCall(0, 0)
    }

    override fun decimalPush(value: ValueDecimal): StackTop = StackTop(
        native.decimalPush(value.toBigDecimal().toString())
    )

    override fun decimalGet(idx: ValueNumber): ValueDecimal = ValueDecimal(BigDecimal(native.decimalGet(idx.intValue)))

    override fun errorPush(error: ValueError) = StackTop(native.errorPush(error.stringValue))
    override fun errorGet(idx: ValueNumber): ValueError = ValueError(native.errorGet(idx.intValue))

    override fun functionPush(value: KuaFunction<*, *, *, *>) = StackTop(native.functionPush(value))

    override fun globalGet(key: ValueString): Value {
        native.globalGet(key.stringValue)
        return get(-1)
    }

    override fun globalGetTable(key: ValueString): KuaTable {
        native.globalGetTable(key.stringValue)
        return tableGet(-1)
    }

    override fun globalSet(key: ValueString, value: Value) {
        push(value)
        native.globalSet(key.stringValue)
    }

    override fun globalUnset(key: ValueString) {
        native.nilPush()
        native.globalSet(key.stringValue)
    }

    override fun nilPush() = StackTop(native.nilPush())
    override fun numberGet(idx: ValueNumber) = ValueNumber(native.numberGet(idx.intValue))
    override fun numberPush(value: ValueNumber) = StackTop(native.numberPush(value.doubleValue))

    override fun referenceAcquire() = KuaReference(native.referenceAcquire())

    override fun referencePush(reference: KuaReference) = luaToType(
        native.referencePush(reference.value)
    )

    override fun referenceRelease(reference: KuaReference) {
        native.referenceRelease(reference.value)
    }

    override fun stringGet(idx: ValueNumber) = ValueString(native.stringGet(idx.intValue))
    override fun stringPush(value: ValueString) = StackTop(native.stringPush(value.stringValue))


    override fun tableAppend(idx: ValueNumber) = TableLength(native.tableAppend(idx.intValue))
    override fun tableCreate(arrayCount: ValueNumber, recordCount: ValueNumber): KuaTable {
        return KuaTable(
            index = ValueNumber(native.tableCreate(arrayCount.intValue, recordCount.intValue)),
            state = this
        )
    }

    override fun tableFieldGet(idx: ValueNumber, key: ValueString) =
        luaToType(native.tableFieldGet(idx.intValue, key.stringValue))

    override fun tableFieldSet(idx: ValueNumber, key: ValueString) =
        TableLength(native.tableFieldSet(idx.intValue, key.stringValue))

    override fun tableGet(idx: ValueNumber) =
        KuaTable(ValueNumber(native.tableGet(native.absIndex(idx.intValue))), this)

    override fun tableLength(idx: ValueNumber) = TableLength(native.tableLength(idx.intValue))
    override fun tableNext(idx: ValueNumber) = ValueBoolean(native.tableNext(idx.intValue))
    override fun tablePush(value: KuaTable) = StackTop(native.topPush(value.index.intValue))
    override fun tableRawSet(idx: ValueNumber) = TableLength(native.tableRawSet(idx.intValue))
    override fun tableRawSetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) =
        TableLength(native.tableRawSetIdx(stackIdx.intValue, tableIdx.intValue))

    override fun tableRawGet(idx: ValueNumber) = luaToType(native.tableRawGet(idx.intValue))
    override fun tableRawGetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) =
        luaToType(native.tableRawGetIdx(stackIdx.intValue, tableIdx.intValue))

    override fun tableSubTableGet(idx: ValueNumber, key: ValueString) =
        luaToType(native.tableSubTableGet(idx.intValue, key.stringValue))

    override fun topGet(): StackTop = StackTop(native.topGet())
    override fun topPop(len: ValueNumber) = StackTop(native.topPop(len.intValue))
    override fun topPush(idx: ValueNumber): StackTop = StackTop(native.topPush(idx.intValue))
    override fun topSet(idx: ValueNumber) = native.topSet(idx.intValue)

    override fun type(idx: ValueNumber) = luaToType(native.type(idx.intValue))

}

fun State.absIndex(idx: Int) = absIndex(ValueNumber(idx))
fun State.get(idx: Int) = get(ValueNumber(idx))
fun State.booleanGet(idx: Int) = booleanGet(ValueNumber(idx))
fun State.decimalGet(idx: Int) = decimalGet(ValueNumber(idx))
fun State.errorGet(idx: Int) = errorGet(ValueNumber(idx))
fun State.numberGet(idx: Int) = numberGet(ValueNumber(idx))
fun State.stringGet(idx: Int) = stringGet(ValueNumber(idx))
fun State.tableAppend(idx: Int) = tableAppend(ValueNumber(idx))
fun State.tableCreate(arrayCount: Int, recordCount: Int) =
    tableCreate(ValueNumber(arrayCount), ValueNumber(recordCount))

fun State.tableFieldGet(idx: Int, key: String) = tableFieldGet(ValueNumber(idx), ValueString(key))
fun State.tableFieldGet(idx: Int, key: ValueString) = tableFieldGet(ValueNumber(idx), key)
fun State.tableFieldSet(idx: Int, key: ValueString) = tableFieldSet(ValueNumber(idx), key)
fun State.tableGet(idx: Int) = tableGet(ValueNumber(idx))
fun State.tableLength(idx: Int) = tableLength(ValueNumber(idx))
fun State.tableNext(idx: Int) = tableNext(ValueNumber(idx))
fun State.tableRawGetIdx(stackIdx: Int, tableIdx: Int) = tableRawGetIdx(ValueNumber(stackIdx), ValueNumber(tableIdx))
fun State.tableRawGet(idx: Int) = tableRawGet(ValueNumber(idx))
fun State.tableRawSet(idx: Int) = tableRawSet(ValueNumber(idx))
fun State.tableRawSetIdx(stackIdx: Int, tableIdx: Int) = tableRawSetIdx(ValueNumber(stackIdx), ValueNumber(tableIdx))
fun State.tableSubTableGet(idx: Int, key: String) = tableSubTableGet(ValueNumber(idx), ValueString(key))
fun State.topPush(idx: Int) = topPush(ValueNumber(idx))
fun State.topPop(idx: Int) = topPop(ValueNumber(idx))
fun State.topSet(idx: Int) = topSet(ValueNumber(idx))


fun State.type(idx: Int) = type(ValueNumber(idx))

interface CloseableState : State, AutoCloseable

class CloseableStateImpl(native: Native = Native()) : StateImpl(native), CloseableState {
    override fun close() {
        native.close()
    }
}

private fun luaToType(value: Int) = when (value) {
    0 -> ValueNil::class
    1 -> ValueBoolean::class
    3 -> ValueNumber::class
    4 -> ValueString::class
    5 -> KuaTable::class
    6 -> KuaFunction::class
    10 -> ValueError::class
    11 -> ValueDecimal::class
    else -> TODO("$value not implemented yet")
}

fun <T : State> T.tableCreate(vararg pairs: Pair<ValueString, Value>): KuaTable = tableCreate(pairs.toMap())


fun <T : State> T.tableCreate(data: Map<ValueString, Value>): KuaTable {
    return tableCreate(0, data.size).also { map ->
        data.forEach { (key, value) ->
            map[key] = value
        }
    }
}

fun <T : State> T.tableCreate(data: List<Value>): KuaTable {
    return tableCreate(data.size, 0).also { array ->
        data.forEach(array::append)
    }
}