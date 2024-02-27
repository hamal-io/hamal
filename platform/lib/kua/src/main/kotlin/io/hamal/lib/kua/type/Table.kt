package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class KuaTable(
    val index: Int,
    val state: State
) : KuaType {

    override val type: KuaType.Type = KuaType.Type.Table

    fun mapEntries(): Sequence<Pair<KuaString, KuaType>> {
        return KuaTableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getStringType(index) },
            valueExtractor = { state, index -> state.getAny(index).value }
        ).asSequence().map { it.key to it.value }
    }

    fun asSequence(): Sequence<KuaType> {
        return KuaTableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index -> state.getAny(index).value }
        ).asSequence().map { it.value }
    }

    fun forEach(action: (KuaType) -> Unit) {
//        state.pushNil()
//        while (state.native.tableNext(index)) {
//            val index = state.getNumberType(state.absIndex(-2))
//            val value = state.getAny(state.absIndex(-1)).value
//            action(value)
//            state.native.pop(1)
//        }
//        state.native.topPop(1)
    }

    fun append(value: KuaType): Int {
        return when (value) {
            is KuaBoolean -> append(value)
            is KuaCode -> append(value)
            is KuaDecimal -> append(value)
            is KuaError -> append(value)
            is KuaFunction<*, *, *, *> -> append(value)
            is KuaNil -> append(value)
            is KuaNumber -> append(value)
            is KuaString -> append(value)
            is KuaTable -> append(value)
            is KuaTable -> append(value)
            else -> TODO()
        }
    }

    fun getTableMap(idx: Int): KuaTable {
        return state.getTableMap(idx)
    }

    fun getTableArray(idx: Int): KuaTable {
        return state.getTableArray(idx)
    }

    fun append(value: KuaAny): Int {
        TODO("Not yet implemented")
    }

    fun type(idx: Int): KClass<out KuaType> {
        TODO("Not yet implemented")
    }

    fun append(value: KuaString) = append(value.value)
    fun get(idx: Int): KuaType {
        return state.getAny(idx).value
    }


    fun getBoolean(idx: Int) = getBooleanType(idx) == KuaTrue
    fun getBooleanValue(idx: Int): KuaBoolean {
        TODO("Not yet implemented")
    }

    fun getBooleanType(idx: Int): KuaBoolean {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.getBooleanValue(-1)
    }

    fun append(value: KuaBoolean) = append(value.value)

    fun append(value: Boolean): Int {
        TODO()
//        state.native.booleanPush(value)
//        return state.tableAppend(index)
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): KuaNumber {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaNumber::class)
        return state.getNumberType(-1)
    }

    fun getDecimalType(idx: Int): KuaDecimal {
        TODO("Not yet implemented")
    }

    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: KuaNumber) = append(value.value)
    fun append(value: Double): Int {
        TODO()
//        state.native.numberPush(value)
//        return state.tableAppend(index)
    }

    fun append(value: KuaDecimal): Int {
        state.decimalPush(value)
        return state.tableAppend(index)
    }

    fun append(value: ValueObjectId) = append(value.value.value)
    fun append(value: SnowflakeId) = append(value.value.toString(16))

    fun getString(idx: Int) = getStringType(idx).value
    fun getStringType(idx: Int): KuaString {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaString::class)
        return state.getStringType(-1)
    }


    fun append(value: String): Int {
        TODO()
//        state.native.stringPush(value)
//        return state.tableAppend(index)
    }

    fun append(value: KuaTable): Int {
        state.pushTable(value)
        return state.tableAppend(index)
    }


    fun getArray(key: String): KuaTable {
        return findArray(key) ?: throw NoSuchElementException("$key not found")
    }

    fun isNull(key: String): Boolean = type(key) == KuaNil::class

    fun findArray(key: String): KuaTable? {
//        if (isNull(key)) {
//            return null
//        }
//
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaTable::class)
//        return state.getTableArray(state.top.value)
        TODO()
    }

    fun forEach(action: (key: KuaType, value: KuaType) -> Unit) {
//        state.pushNil()
//        while (state.native.tableNext(index)) {
//            val key = state.getStringType(state.absIndex(-2))
//            val value = state.getAny(state.absIndex(-1))
//            action(key, value)
////            state.native.pop(1)
//        }
//        state.native.topPop(1)
        TODO()
    }

    fun get(key: String): KuaAny {
//        state.pushString(key)
//        return state.getAny(state.top.value)
        TODO()
    }

    operator fun set(key: String, value: KuaAny): Int {
        return set(key, value.value)
    }

    operator fun set(key: KuaString, value: KuaAny): Int {
        return set(key.value, value.value)
    }

    operator fun set(key: KuaString, value: KuaType): Int {
        return set(key.value, value)
    }

    operator fun set(key: String, value: KuaType): Int {
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
            is KuaTable -> set(key, value)
            else -> TODO()
        }
    }

//    val length get() : Int = state.native.tableGetLength(index)
    val length get() : Int = TODO()

    fun unset(key: KuaString) = unset(key.value)


    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: String, value: KuaNil) = unset(key)

    @Suppress("UNUSED_PARAMETER")
    operator fun set(key: KuaString, value: KuaNil) = unset(key.value)
    fun unset(key: String): Int {
        state.pushString(key)
        state.pushNil()
        return state.tableSetRaw(index)
    }

    fun getBooleanValue(key: String): KuaBoolean {
        TODO("Not yet implemented")
    }

    operator fun set(key: String, value: KuaBoolean) = set(key, value.value)
    operator fun set(key: KuaString, value: KuaBoolean) = set(key.value, value.value)
    operator fun set(key: String, value: Boolean): Int {
        state.pushString(key)
        state.pushBoolean(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: KuaCode) = set(key, value.value)
    operator fun set(key: KuaString, value: KuaCode) = set(key.value, value)

    operator fun set(key: String, value: Int) = set(key, value.toDouble())
    operator fun set(key: String, value: Long) = set(key, value.toDouble())
    operator fun set(key: String, value: Float) = set(key, value.toDouble())
    operator fun set(key: String, value: KuaNumber) = set(key, value.value)
    operator fun set(key: KuaString, value: KuaNumber) = set(key.value, value.value)
    operator fun set(key: String, value: Double): Int {
        state.pushString(key)
        state.pushNumber(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: KuaDecimal): Int {
        state.pushString(key)
        state.decimalPush(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
    operator fun set(key: KuaString, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
    operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    operator fun set(key: KuaString, value: SnowflakeId) = set(key.value, value.value.toString(16))

    operator fun set(key: String, value: KuaString) = set(key, value.value)
    operator fun set(key: KuaString, value: KuaString) = set(key.value, value.value)
    operator fun set(key: String, value: String): Int {
        state.pushString(key)
        state.pushString(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: KuaString, value: KuaFunction<*, *, *, *>) = set(key.value, value)
    operator fun set(key: String, value: KuaFunction<*, *, *, *>): Int {
        state.pushString(key)
        state.pushFunction(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: KuaTable): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    fun getTableMap(key: String): KuaTable {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaTable::class)
//        return state.getTableMap(state.top.value)
        TODO()
    }

    fun getBooleanType(key: KuaString): KuaBoolean = getBooleanType(key.value)
    fun getBoolean(key: String): Boolean = getBooleanType(key).value
    fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
    fun getBooleanType(key: String): KuaBoolean {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaBoolean::class)
//        return booleanOf(state.native.booleanGet(state.top.value)).also { state.native.topPop(1) }
        TODO()
    }

    fun getCode(key: KuaString): KuaCode = getCode(key.value)
    fun getCode(key: String): KuaCode {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaString::class)
//        return KuaCode(state.getString(state.top.value)).also { state.native.topPop(1) }
        TODO()
    }

    fun getNumberType(key: KuaString): KuaNumber = getNumberType(key.value)
    fun getInt(key: String): Int = getNumberType(key).value.toInt()
    fun getInt(key: KuaString) = getInt(key.value)
    fun getLong(key: String): Long = getNumberType(key).value.toLong()
    fun getLong(key: KuaString): Long = getLong(key.value)
    fun getFloat(key: String): Float = getNumberType(key).value.toFloat()
    fun getFloat(key: KuaString): Float = getFloat(key.value)
    fun getDouble(key: String): Double = getNumberType(key).value
    fun getDouble(key: KuaString): Double = getDouble(key.value)
    fun getNumberValue(key: String): KuaNumber {
        TODO("Not yet implemented")
    }

    fun getNumberType(key: String): KuaNumber {
        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaNumber::class)
//        return KuaNumber(state.native.numberGet(state.top.value)).also { state.native.topPop(1) }
        TODO()
    }

    fun getDecimalType(key: String): KuaDecimal {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaDecimal::class)
//        return state.decimalGet(state.top.value).also { state.native.topPop(1) }
        TODO()
    }


    fun getStringType(key: KuaString): KuaString = getStringType(key.value)
    fun getString(key: String): String = getStringType(key).value
    fun getString(key: KuaString): String = getString(key.value)
    fun findString(key: String): String? {
        return findStringType(key)?.value
    }

    fun findStringType(key: String): KuaString? {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        if (type == KuaNil::class) {
//            return null
//        }
//        type.checkExpectedType(KuaString::class)
//        return KuaString(state.native.stringGet(state.top.value).also { state.native.topPop(1) })
        TODO()
    }

    fun getStringType(key: String): KuaString {
//        state.pushString(key)
//        val type = state.tableGetRaw(index)
//        type.checkExpectedType(KuaString::class)
//        return KuaString(state.native.stringGet(state.top.value)).also { state.native.topPop(1) }
        TODO()
    }

    fun type(key: String): KClass<out KuaType> {
        state.pushString(key)
        return state.tableGetRaw(index)
    }

}

private fun KClass<out KuaType>.checkExpectedType(expected: KClass<out KuaType>) {
    check(this == expected) {
        "Expected type to be $expected but was $this"
    }
}


fun State.tableKeyType(idx: Int): KClass<out KuaType> {
//    native.nilPush()
//    native.tableNext(idx)
//    val result = type(-2)
//    native.topPop(2)
//    return result
    TODO()
}

