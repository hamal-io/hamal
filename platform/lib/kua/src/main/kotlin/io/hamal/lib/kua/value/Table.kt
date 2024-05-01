package io.hamal.lib.kua.value

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.*
import kotlin.reflect.KClass

class KuaTable(
    val index: ValueNumber,
    val state: State
) : KuaValue {

    constructor(index: Int, state: State) : this(ValueNumber(index), state)

    val length get() : TableLength = state.tableLength(index)

    fun append(value: Value): TableLength {
        return when (value) {
            is ValueBoolean -> {
                state.booleanPush(value)
                state.tableAppend(index)
            }

            is ValueDecimal -> {
                state.decimalPush(value)
                state.tableAppend(index)
            }

            is ValueError -> {
                state.errorPush(value)
                state.tableAppend(index)

            }

            is KuaFunction<*, *, *, *> -> {
                state.functionPush(value)
                state.tableAppend(index)
            }

            is ValueSnowflakeId -> {
                state.stringPush(ValueString(value.stringValue))
                state.tableAppend(index)
            }

            is ValueVariableSnowflakeId -> {
                state.stringPush(ValueString(value.stringValue))
                state.tableAppend(index)
            }

            is ValueNil -> {
                state.nilPush()
                state.tableAppend(index)
            }

            is ValueNumber -> {
                state.numberPush(value)
                state.tableAppend(index)
            }

            is ValueVariableNumber -> {
                state.numberPush(value.value)
                state.tableAppend(index)
            }

            is ValueString -> {
                state.stringPush(value)
                state.tableAppend(index)
            }

            is ValueVariableString -> {
                state.stringPush(value.value)
                state.tableAppend(index)
            }

            is KuaTable -> {
                state.tablePush(value)
                state.tableAppend(index)
            }

            else -> TODO("$value not supported yet")
        }
    }

    fun asEntries(): Sequence<Pair<ValueString, Value>> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.stringGet(index) },
            valueExtractor = { state, index -> state.get(index) }
        ).asSequence().map { it.key to it.value }
    }

    fun asList(): Sequence<Value> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.get(index) }
        ).asSequence().map { it.value }
    }

    fun get(idx: ValueNumber): Value {
        return state.get(idx)
    }

    fun getBoolean(idx: ValueNumber): ValueBoolean {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueBoolean::class)
        return state.booleanGet(-1)
    }

    fun getDecimal(idx: ValueNumber): ValueDecimal {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueDecimal::class)
        return state.decimalGet(-1)
    }

    fun getError(idx: ValueNumber): ValueError {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueError::class)
        return state.errorGet(-1)
    }

    fun getNil(idx: ValueNumber): ValueNil {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueNil::class)
        return ValueNil
    }

    fun getNumber(idx: ValueNumber): ValueNumber {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueNumber::class)
        return state.numberGet(-1)
    }

    fun getString(idx: ValueNumber): ValueString {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(ValueString::class)
        return state.stringGet(-1)
    }


    operator fun set(key: ValueString, value: Value): TableLength {
        return when (value) {
            is ValueBoolean -> {
                state.stringPush(key)
                state.booleanPush(if (value.booleanValue) ValueTrue else ValueFalse)
                state.tableRawSet(index)

            }

            is ValueCode -> {
                state.stringPush(key)
                state.stringPush(ValueString(value.stringValue))
                state.tableRawSet(index)
            }

            is ValueDecimal -> {
                state.stringPush(key)
                state.decimalPush(value)
                state.tableRawSet(index)

            }

            is ValueError -> {
                state.stringPush(key)
                state.errorPush(value)
                state.tableRawSet(index)
            }

            is KuaFunction<*, *, *, *> -> {
                state.stringPush(key)
                state.functionPush(value)
                state.tableRawSet(index)
            }

            is ValueSnowflakeId -> {
                state.stringPush(key)
                state.stringPush(ValueString(value.stringValue))
                state.tableRawSet(index)
            }

            is ValueVariableSnowflakeId -> {
                state.stringPush(key)
                state.stringPush(ValueString(value.stringValue))
                state.tableRawSet(index)
            }

            is ValueNil -> {
                state.stringPush(key)
                state.nilPush()
                state.tableRawSet(index)
            }

            is ValueNumber -> {
                state.stringPush(key)
                state.numberPush(value)
                state.tableRawSet(index)
            }

            is ValueVariableNumber -> {
                state.stringPush(key)
                state.numberPush(value.value)
                state.tableRawSet(index)
            }

            is ValueString -> {
                state.stringPush(key)
                state.stringPush(value)
                state.tableRawSet(index)
            }

            is ValueVariableString -> {
                state.stringPush(key)
                state.stringPush(value.value)
                state.tableRawSet(index)
            }

            is KuaTable -> {
                state.stringPush(key)
                state.tablePush(value)
                state.tableRawSet(index)
            }

            else -> TODO("$value of class ${value::class} not supported yet")
        }
    }

    fun get(key: ValueString): Value {
        state.tableFieldGet(index, key)
        return state.get(-1)
    }

    fun findBoolean(key: ValueString): ValueBoolean? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(ValueBoolean::class)
        return state.booleanGet(-1).also { state.topPop(1) }
    }

    fun getBoolean(key: ValueString): ValueBoolean {
        return findBoolean(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findDecimal(key: ValueString): ValueDecimal? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(ValueDecimal::class)
        return state.decimalGet(-1).also { state.topPop(1) }
    }

    fun getDecimal(key: ValueString): ValueDecimal {
        return findDecimal(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findError(key: ValueString): ValueError? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(ValueError::class)
        return state.errorGet(-1).also { state.topPop(1) }
    }

    fun getError(key: ValueString): ValueError {
        return findError(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findNumber(key: ValueString): ValueNumber? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(ValueNumber::class)
        return state.numberGet(-1).also { state.topPop(1) }
    }

    fun getNumber(key: ValueString): ValueNumber {
        return findNumber(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findString(key: ValueString): ValueString? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(ValueString::class)
        return state.stringGet(-1).also { state.topPop(1) }
    }

    fun getString(key: ValueString): ValueString {
        return findString(key) ?: throw NoSuchElementException("$key not found")
    }


    fun findTable(key: ValueString): KuaTable? {
        if (isNull(key)) {
            return null
        }

        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaTable::class)
        return state.tableGet(-1)
    }

    fun getTable(key: ValueString): KuaTable {
        return findTable(key) ?: throw NoSuchElementException("$key not found")
    }

    fun isNull(key: ValueString): Boolean = type(key) == ValueNil::class

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: ValueString, value: ValueNil) = unset(key)
    fun unset(key: ValueString): TableLength {
        state.stringPush(key)
        state.nilPush()
        return state.tableRawSet(index)
    }


    fun type(idx: ValueNumber): KClass<out Value> {
        state.tableGet(idx)
        return state.tableRawGet(index)
    }

    fun type(key: ValueString): KClass<out Value> {
        state.stringPush(key)
        return state.tableRawGet(index)
    }
}


fun KuaTable.unset(key: String) = unset(ValueString(key))
operator fun KuaTable.set(key: String, value: KuaValue) = set(ValueString(key), value)
operator fun KuaTable.set(key: String, value: Value) = set(ValueString(key), value)
operator fun KuaTable.set(key: String, value: Boolean) = set(ValueString(key), ValueBoolean.of(value))
operator fun KuaTable.set(key: String, value: Int) = set(ValueString(key), ValueNumber(value))
operator fun KuaTable.set(key: String, value: Long) = set(ValueString(key), ValueNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Float) = set(ValueString(key), ValueNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Double) = set(ValueString(key), ValueNumber(value))
operator fun KuaTable.set(key: String, value: String) = set(ValueString(key), ValueString(value))
operator fun KuaTable.set(key: String, value: KuaTable) = set(ValueString(key), value)

fun KuaTable.get(idx: Int) = get(ValueNumber(idx))
fun KuaTable.getBoolean(idx: Int) = getBoolean(ValueNumber(idx))
fun KuaTable.getDecimal(idx: Int) = getDecimal(ValueNumber(idx))
fun KuaTable.getError(idx: Int) = getError(ValueNumber(idx))
fun KuaTable.getNil(idx: Int) = getNil(ValueNumber(idx))
fun KuaTable.getNumber(idx: Int) = getNumber(ValueNumber(idx))
fun KuaTable.getString(idx: Int) = getString(ValueNumber(idx))


fun KuaTable.findString(key: String) = findString(ValueString(key))
fun KuaTable.findTable(key: String) = findTable(ValueString(key))

fun KuaTable.get(key: String) = get(ValueString(key))
fun KuaTable.getBoolean(key: String) = getBoolean(ValueString(key))
fun KuaTable.getNumber(key: String) = getNumber(ValueString(key))
fun KuaTable.getInt(key: String) = getNumber(key).intValue
fun KuaTable.getLong(key: String) = getNumber(key).longValue
fun KuaTable.getString(key: String) = getString(ValueString(key))
fun KuaTable.getTable(key: String) = getTable(ValueString(key))

fun KuaTable.type(idx: Int): KClass<out Value> = type(ValueNumber(idx))
fun KuaTable.type(key: String): KClass<out Value> = type(ValueString(key))
