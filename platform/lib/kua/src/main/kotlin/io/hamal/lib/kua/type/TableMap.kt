package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class KuaTableMap(
    override val index: Int,
    override val state: State
) : KuaTable {

    override val type: KuaType.Type = KuaType.Type.Table

    fun asSequence(): Sequence<Pair<KuaString, KuaType>> {
        return KuaTableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getStringType(index) },
            valueExtractor = { state, index -> state.getAny(index).value }
        ).asSequence().map { it.key to it.value }
    }

    fun getArray(key: String): KuaTableArray {
        return findArray(key) ?: throw NoSuchElementException("$key not found")
    }

    fun isNull(key: String): Boolean = type(key) == KuaNil::class

    fun findArray(key: String): KuaTableArray? {
        if (isNull(key)) {
            return null
        }

        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaTable::class)
        return state.getTableArray(state.top.value)
    }


    fun get(key: String): KuaAny {
        state.pushString(key)
        return state.getAny(state.top.value)
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
            is KuaTableArray -> set(key, value)
            is KuaTableMap -> set(key, value)
            else -> TODO()
        }
    }

    val length get() = state.native.tableGetLength(index)

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
        state.native.pushDecimal(value)
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

    operator fun set(key: String, value: KuaTableMap): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    fun getTableMap(key: String): KuaTableMap {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaTable::class)
        return state.getTableMap(state.top.value)
    }

    fun getBooleanType(key: KuaString): KuaBoolean = getBooleanType(key.value)
    fun getBoolean(key: String): Boolean = getBooleanType(key).value
    fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
    fun getBooleanType(key: String): KuaBoolean {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaBoolean::class)
        return booleanOf(state.native.toBoolean(state.top.value)).also { state.native.pop(1) }
    }

    fun getCode(key: KuaString): KuaCode = getCode(key.value)
    fun getCode(key: String): KuaCode {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaString::class)
        return KuaCode(state.getString(state.top.value)).also { state.native.pop(1) }
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
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaNumber::class)
        return KuaNumber(state.native.toNumber(state.top.value)).also { state.native.pop(1) }
    }

    fun getDecimalType(key: String): KuaDecimal {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaDecimal::class)
        return state.native.toDecimal(state.top.value).also { state.native.pop(1) }
    }


    fun getStringType(key: KuaString): KuaString = getStringType(key.value)
    fun getString(key: String): String = getStringType(key).value
    fun getString(key: KuaString): String = getString(key.value)
    fun findString(key: String): String? {
        return findStringType(key)?.value
    }

    fun findStringType(key: String): KuaString? {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        if (type == KuaNil::class) {
            return null
        }
        type.checkExpectedType(KuaString::class)
        return KuaString(state.native.toString(state.top.value).also { state.native.pop(1) })
    }

    fun getStringType(key: String): KuaString {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaString::class)
        return KuaString(state.native.toString(state.top.value)).also { state.native.pop(1) }
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
    native.pushNil()
    native.tableNext(idx)
    val result = type(-2)
    native.pop(2)
    return result
}