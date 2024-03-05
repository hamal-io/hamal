package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.TableLength
import kotlin.reflect.KClass

class KuaTable(
    val index: Int,
    val state: State
) : KuaType {

    override val type: KuaType.Type = KuaType.Type.Table
    val length get() : TableLength = state.tableLength(index)

    fun append(value: KuaType): TableLength {
        return when (value) {
            is KuaBoolean -> {
                state.booleanPush(value)
                state.tableAppend(index)
            }

            is KuaDecimal -> {
                state.decimalPush(value)
                state.tableAppend(index)
            }

            is KuaError -> {
                state.errorPush(value)
                state.tableAppend(index)

            }

            is KuaFunction<*, *, *, *> -> {
                state.functionPush(value)
                state.tableAppend(index)
            }

            is KuaNil -> {
                state.nilPush()
                state.tableAppend(index)
            }

            is KuaNumber -> {
                state.numberPush(value)
                state.tableAppend(index)
            }

            is KuaString -> {
                state.stringPush(value)
                state.tableAppend(index)
            }

            is KuaTable -> {
                state.tablePush(value)
                state.tableAppend(index)
            }

            else -> TODO()
        }
    }

    fun asEntries(): Sequence<Pair<KuaString, KuaType>> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.stringGet(index) },
            valueExtractor = { state, index -> state.get(index) }
        ).asSequence().map { it.key to it.value }
    }

    fun asList(): Sequence<KuaType> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.get(index) }
        ).asSequence().map { it.value }
    }

    fun get(idx: Int): KuaType {
        return state.get(idx)
    }

    fun getBoolean(idx: Int): KuaBoolean {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.booleanGet(-1)
    }

    fun getDecimal(idx: Int): KuaDecimal {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaDecimal::class)
        return state.decimalGet(-1)
    }

    fun getError(idx: Int): KuaError {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaError::class)
        return state.errorGet(-1)
    }

    fun getNil(idx: Int): KuaNil {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaNil::class)
        return KuaNil
    }

    fun getNumber(idx: Int): KuaNumber {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaNumber::class)
        return state.numberGet(-1)
    }

    fun getString(idx: Int): KuaString {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaString::class)
        return state.stringGet(-1)
    }


    operator fun set(key: KuaString, value: KuaType): TableLength {
        return when (value) {
            is KuaBoolean -> {
                state.stringPush(key)
                state.booleanPush(if (value.booleanValue) KuaTrue else KuaFalse)
                state.tableRawSet(index)

            }

            is KuaCode -> {
                state.stringPush(key)
                state.stringPush(KuaString(value.stringValue))
                state.tableRawSet(index)
            }

            is KuaDecimal -> {
                state.stringPush(key)
                state.decimalPush(value)
                state.tableRawSet(index)

            }

            is KuaError -> {
                state.stringPush(key)
                state.errorPush(value)
                state.tableRawSet(index)
            }

            is KuaFunction<*, *, *, *> -> {
                state.stringPush(key)
                state.functionPush(value)
                state.tableRawSet(index)
            }

            is KuaNil -> {
                state.stringPush(key)
                state.nilPush()
                state.tableRawSet(index)
            }

            is KuaNumber -> {
                state.stringPush(key)
                state.numberPush(value)
                state.tableRawSet(index)

            }

            is KuaString -> {
                state.stringPush(key)
                state.stringPush(value)
                state.tableRawSet(index)
            }

            is KuaTable -> {
                state.stringPush(key)
                state.tablePush(value)
                state.tableRawSet(index)
            }

            else -> TODO()
        }
    }

    fun get(key: KuaString): KuaType {
        state.tableFieldGet(index, key)
        return state.get(-1)
    }

    fun findBoolean(key: KuaString): KuaBoolean? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaBoolean::class)
        return state.booleanGet(-1).also { state.topPop(1) }
    }

    fun getBoolean(key: KuaString): KuaBoolean {
        return findBoolean(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findDecimal(key: KuaString): KuaDecimal? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaDecimal::class)
        return state.decimalGet(-1).also { state.topPop(1) }
    }

    fun getDecimal(key: KuaString): KuaDecimal {
        return findDecimal(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findError(key: KuaString): KuaError? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaError::class)
        return state.errorGet(-1).also { state.topPop(1) }
    }

    fun getError(key: KuaString): KuaError {
        return findError(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findNumber(key: KuaString): KuaNumber? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaNumber::class)
        return state.numberGet(-1).also { state.topPop(1) }
    }

    fun getNumber(key: KuaString): KuaNumber {
        return findNumber(key) ?: throw NoSuchElementException("$key not found")
    }

    fun findString(key: KuaString): KuaString? {
        if (isNull(key)) {
            return null
        }
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaString::class)
        return state.stringGet(-1).also { state.topPop(1) }
    }

    fun getString(key: KuaString): KuaString {
        return findString(key) ?: throw NoSuchElementException("$key not found")
    }


    fun findTable(key: KuaString): KuaTable? {
        if (isNull(key)) {
            return null
        }

        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaTable::class)
        return state.tableGet(-1)
    }

    fun getTable(key: KuaString): KuaTable {
        return findTable(key) ?: throw NoSuchElementException("$key not found")
    }

    fun isNull(key: KuaString): Boolean = type(key) == KuaNil::class

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: KuaString, value: KuaNil) = unset(key)
    fun unset(key: KuaString): TableLength {
        state.stringPush(key)
        state.nilPush()
        return state.tableRawSet(index)
    }


    fun type(idx: Int): KClass<out KuaType> {
        state.tableGet(idx)
        return state.tableRawGet(index)
    }

    fun type(key: KuaString): KClass<out KuaType> {
        state.stringPush(key)
        return state.tableRawGet(index)
    }

}


fun KuaTable.unset(key: String) = unset(KuaString(key))
operator fun KuaTable.set(key: String, value: KuaType) = set(KuaString(key), value)
operator fun KuaTable.set(key: String, value: Boolean) = set(KuaString(key), KuaBoolean.of(value))
operator fun KuaTable.set(key: String, value: Int) = set(KuaString(key), KuaNumber(value))
operator fun KuaTable.set(key: String, value: Long) = set(KuaString(key), KuaNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Float) = set(KuaString(key), KuaNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Double) = set(KuaString(key), KuaNumber(value))
operator fun KuaTable.set(key: String, value: String) = set(KuaString(key), KuaString(value))

fun KuaTable.findString(key: String): KuaString? = findString(KuaString(key))
fun KuaTable.findTable(key: String): KuaTable? = findTable(KuaString(key))

fun KuaTable.getBoolean(key: String): KuaBoolean = getBoolean(KuaString(key))
fun KuaTable.getString(key: String): KuaString = getString(KuaString(key))
fun KuaTable.getNumber(key: String): KuaNumber = getNumber(KuaString(key))

fun KuaTable.type(key: String): KClass<out KuaType> = type(KuaString(key))
