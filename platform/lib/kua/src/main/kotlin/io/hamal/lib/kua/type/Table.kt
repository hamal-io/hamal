package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.TableLength
import kotlin.reflect.KClass

class KuaTable(
    val index: Int,
    val state: State
) : KuaType {

    override val type: KuaType.Type = KuaType.Type.Table

    fun mapEntries(): Sequence<Pair<KuaString, KuaType>> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.stringGet(index) },
            valueExtractor = { state, index -> state.anyGet(index).value }
        ).asSequence().map { it.key to it.value }
    }

    fun asSequence(): Sequence<KuaType> {
        return KuaTableIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.anyGet(index).value }
        ).asSequence().map { it.value }
    }


    fun append(value: KuaType): TableLength {
        return when (value) {
            is KuaBoolean -> append(value)
            is KuaDecimal -> append(value)
            is KuaError -> append(value)
            is KuaFunction<*, *, *, *> -> append(value)
            is KuaNil -> append(value)
            is KuaNumber -> append(value)
            is KuaString -> append(value)
            is KuaTable -> append(value)
            else -> TODO()
        }
    }


    fun get(idx: Int): KuaType {
        return state.anyGet(idx).value
    }


    fun getBoolean(idx: Int): KuaBoolean {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.booleanGet(-1)
    }

    fun append(value: KuaBoolean): TableLength {
        state.booleanPush(value)
        return state.tableAppend(index)
    }


    fun append(value: KuaError): TableLength {
        state.errorPush(value)
        return state.tableAppend(index)
    }

    fun getNumber(idx: Int): KuaNumber {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaNumber::class)
        return state.numberGet(-1)
    }


    fun append(value: KuaNumber): TableLength {
        state.numberPush(value)
        return state.tableAppend(index)
    }

    fun append(@Suppress("UNUSED_PARAMETER") value: KuaNil): TableLength {
        state.nilPush()
        return state.tableAppend(index)
    }


    fun append(value: KuaDecimal): TableLength {
        state.decimalPush(value)
        return state.tableAppend(index)
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


    fun getString(idx: Int): KuaString {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaString::class)
        return state.stringGet(-1)
    }

    fun getDecimal(idx: Int): KuaDecimal {
        val type = state.tableRawGetIdx(index, idx)
        type.checkExpectedType(KuaDecimal::class)
        return state.decimalGet(-1)
    }


    fun append(value: KuaString): TableLength {
        state.stringPush(value)
        return state.tableAppend(index)
    }


    fun append(value: KuaTable): TableLength {
        state.tablePush(value)
        return state.tableAppend(index)
    }

    fun isNull(key: KuaString): Boolean = type(key) == KuaNil::class

    fun findTable(key: KuaString): KuaTable? {
        if (isNull(key)) {
            return null
        }

        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaTable::class)
        return state.tableGet(-1)
    }

    fun get(key: KuaString): KuaType {
        state.tableFieldGet(index, key)
        return state.anyGet(-1).value
    }

    operator fun set(key: KuaString, value: KuaTable): TableLength {
        state.stringPush(key)
        state.tablePush(value)
        return state.tableRawSet(index)
    }


    operator fun set(key: KuaString, value: KuaDecimal): TableLength {
        state.stringPush(key)
        state.decimalPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaError): TableLength {
        state.stringPush(key)
        state.errorPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaType): TableLength {
        return when (value) {
            is KuaBoolean -> set(key, value)
            is KuaCode -> set(key, value)
            is KuaDecimal -> set(key, value)
            is KuaError -> set(key, value)
            is KuaFunction<*, *, *, *> -> set(key, value)
            is KuaNil -> unset(key)
            is KuaNumber -> set(key, value)
            is KuaString -> set(key, value)
            is KuaTable -> set(key, value)
            else -> TODO()
        }
    }

    val length get() : TableLength = state.tableLength(index)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: String, value: KuaNil) = unset(key)

    fun unset(key: String) = unset(KuaString(key))

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: KuaString, value: KuaNil) = unset(key)
    fun unset(key: KuaString): TableLength {
        state.stringPush(key)
        state.nilPush()
        return state.tableRawSet(index)
    }


    operator fun set(key: KuaString, value: KuaBoolean): TableLength {
        state.stringPush(key)
        state.booleanPush(if (value.booleanValue) KuaTrue else KuaFalse)
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaNumber): TableLength {
        state.stringPush(key)
        state.numberPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: String, value: KuaDecimal): TableLength {
        state.stringPush(KuaString(key))
        state.decimalPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaCode): TableLength {
        state.stringPush(key)
        state.stringPush(KuaString(value.stringValue))
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaString): TableLength {
        state.stringPush(key)
        state.stringPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: KuaString, value: KuaFunction<*, *, *, *>): TableLength {
        state.stringPush(key)
        state.functionPush(value)
        return state.tableRawSet(index)
    }

    operator fun set(key: String, value: KuaTable): TableLength {
        state.stringPush(KuaString(key))
        state.tablePush(value)
        return state.tableRawSet(index)
    }

    fun getBoolean(key: String) = getBoolean(KuaString(key))
    fun getBoolean(key: KuaString): KuaBoolean {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaBoolean::class)
        return state.booleanGet(-1).also { state.topPop(1) }
    }


    fun getError(key: KuaString): KuaError {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaError::class)
        return state.errorGet(-1).also { state.topPop(1) }
    }


    fun getNumber(key: KuaString): KuaNumber {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaNumber::class)
        return state.numberGet(-1).also { state.topPop(1) }
    }


    fun getDecimal(key: KuaString): KuaDecimal {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaDecimal::class)
        return state.decimalGet(-1).also { state.topPop(1) }
    }


    fun findString(key: KuaString): KuaString? {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        if (type == KuaNil::class) {
            return null
        }
        type.checkExpectedType(KuaString::class)
        return state.stringGet(-1).also { state.topPop(1) }
    }


    fun getString(key: KuaString): KuaString {
        state.stringPush(key)
        val type = state.tableRawGet(index)
        type.checkExpectedType(KuaString::class)
        return state.stringGet(state.topGet().value).also { state.topPop(1) }
    }

    fun getTable(key: KuaString): KuaTable {
        val type = state.tableSubTableGet(index, key)
        type.checkExpectedType(KuaTable::class)
        return state.tableGet(-1)
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

operator fun KuaTable.set(key: String, value: Boolean) = set(KuaString(key), KuaBoolean.of(value))
operator fun KuaTable.set(key: String, value: KuaBoolean) = set(KuaString(key), value)
operator fun KuaTable.set(key: String, value: KuaType) = set(KuaString(key), value)
operator fun KuaTable.set(key: String, value: Int) = set(KuaString(key), KuaNumber(value))
operator fun KuaTable.set(key: String, value: Long) = set(KuaString(key), KuaNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Float) = set(KuaString(key), KuaNumber(value.toDouble()))
operator fun KuaTable.set(key: String, value: Double) = set(KuaString(key), KuaNumber(value))
operator fun KuaTable.set(key: String, value: String) = set(KuaString(key), KuaString(value))

fun KuaTable.findString(key: String): KuaString? = findString(KuaString(key))
fun KuaTable.findTable(key: String): KuaTable? = findTable(KuaString(key))

fun KuaTable.getString(key: String): KuaString = getString(KuaString(key))
fun KuaTable.getNumber(key: String): KuaNumber = getNumber(KuaString(key))

fun KuaTable.type(key: String): KClass<out KuaType> = type(KuaString(key))
