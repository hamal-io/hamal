package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.State
import kotlin.reflect.KClass

class TableProxyMap(
    val index: Int,
    val state: State
) : KuaTable.Map {

    companion object {
        fun create(state: State, data: Map<String, KuaType>): TableProxyMap {
            return state.tableCreateMap(data.size).also {
                data.entries.forEach { (key, value) ->
                    it[key] = value
                }
            }
        }
    }

    override fun entries(): Sequence<Pair<KuaString, KuaType>> {
        return TableEntryIterator(
            index = index,
            state = state,
            keyExtractor = { state, index -> state.getStringType(index) },
            valueExtractor = { state, index ->
                when (val value = state.getAny(index).value) {
//                is KuaTable.Array,
                    is KuaBoolean,
                    is KuaDecimal,
//                is KuaTable.Map,
                    is KuaNumber,
                    is KuaString -> value

                    else -> TODO("$value")
                }
            }
        ).asSequence().map { it.key to it.value }
    }

    override fun getArray(key: String): KuaTable.Array {
        return findArray(key) ?: throw NoSuchElementException("$key not found")
    }

    override fun findArray(key: String): KuaTable.Array? {
        if (isNull(key)) {
            return null
        }

        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaTableType::class)
        return state.getTableProxyArray(state.top.value)
    }


    override fun get(key: String): KuaType {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: KuaType): Int {
        return when (value) {
            is KuaString -> set(key, value)
            is KuaNil -> unset(key)
            else -> TODO()
        }
    }


    override val size: Int get() = 0
    override val isArray: Boolean get() = false
    override val isMap: Boolean get() = true


    override val type: KuaType.Type = KuaType.Type.Table

    val length get() = state.native.tableGetLength(index)

    override fun unset(key: KuaString) = unset(key.value)


    @Suppress("UNUSED_PARAMETER")
    override operator fun set(key: String, value: KuaNil) = unset(key)

    @Suppress("UNUSED_PARAMETER")
    override operator fun set(key: KuaString, value: KuaNil) = unset(key.value)
    override fun unset(key: String): Int {
        state.pushString(key)
        state.pushNil()
        return state.tableSetRaw(index)
    }

    override fun getBooleanValue(key: String): KuaBoolean {
        TODO("Not yet implemented")
    }

    override operator fun set(key: String, value: KuaBoolean) = set(key, value.value)
    override operator fun set(key: KuaString, value: KuaBoolean) = set(key.value, value.value)
    override operator fun set(key: String, value: Boolean): Int {
        state.pushString(key)
        state.pushBoolean(value)
        return state.tableSetRaw(index)
    }

    override operator fun set(key: String, value: KuaCode) = set(key, value.value)
    override operator fun set(key: KuaString, value: KuaCode) = set(key.value, value)

    override operator fun set(key: String, value: Int) = set(key, value.toDouble())
    override operator fun set(key: String, value: Long) = set(key, value.toDouble())
    override operator fun set(key: String, value: Float) = set(key, value.toDouble())
    override operator fun set(key: String, value: KuaNumber) = set(key, value.value)
    override fun set(key: String, value: KuaTable): Int {
        TODO("Not yet implemented")
    }

    override operator fun set(key: KuaString, value: KuaNumber) = set(key.value, value.value)
    override operator fun set(key: String, value: Double): Int {
        state.pushString(key)
        state.pushNumber(value)
        return state.tableSetRaw(index)
    }

    operator fun set(key: String, value: KuaDecimal): Int {
        state.pushString(key)
        state.native.pushDecimal(value)
        return state.tableSetRaw(index)
    }

    override operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
    override operator fun set(key: KuaString, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
    override operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    override operator fun set(key: KuaString, value: SnowflakeId) = set(key.value, value.value.toString(16))

    override operator fun set(key: String, value: KuaString) = set(key, value.value)
    override operator fun set(key: KuaString, value: KuaString) = set(key.value, value.value)
    override operator fun set(key: String, value: String): Int {
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

    operator fun set(key: String, value: TableProxyMap): Int {
        state.pushString(key)
        state.pushTable(value)
        return state.tableSetRaw(index)
    }

    fun getTable(key: String): TableProxyMap {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaTableType::class)
        return state.getTableProxyMap(state.top.value)
    }

    fun getBooleanType(key: KuaString): KuaBoolean = getBooleanType(key.value)
    override fun getBoolean(key: String): Boolean = getBooleanType(key).value
    override fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
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
    override fun getInt(key: String): Int = getNumberType(key).value.toInt()
    override fun getInt(key: KuaString) = getInt(key.value)
    override fun getLong(key: String): Long = getNumberType(key).value.toLong()
    override fun getLong(key: KuaString): Long = getLong(key.value)
    override fun getFloat(key: String): Float = getNumberType(key).value.toFloat()
    override fun getFloat(key: KuaString): Float = getFloat(key.value)
    override fun getDouble(key: String): Double = getNumberType(key).value
    override fun getDouble(key: KuaString): Double = getDouble(key.value)
    override fun getNumberValue(key: String): KuaNumber {
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

    override fun getStringType(key: KuaString): KuaString = getStringType(key.value)
    override fun getString(key: String): String = getStringType(key).value
    override fun getString(key: KuaString): String = getString(key.value)
    override fun findStringType(key: String): KuaString? {
        if (isNull(key)) {
            return null
        }

        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaString::class)
        return KuaString(state.native.toString(state.top.value).also { state.native.pop(1) })
    }

    override fun getStringType(key: String): KuaString {
        state.pushString(key)
        val type = state.tableGetRaw(index)
        type.checkExpectedType(KuaString::class)
        return KuaString(state.native.toString(state.top.value)).also { state.native.pop(1) }
    }

    override fun type(key: String): KClass<out KuaType> {
        state.pushString(key)
        return state.tableGetRaw(index)
    }


    fun getBoolean(idx: Int) = getBooleanType(idx) == KuaTrue
    fun getBooleanType(idx: Int): KuaBoolean {
        val type = state.tableGetRawIdx(index, idx)
        type.checkExpectedType(KuaBoolean::class)
        return state.getBooleanValue(-1)
    }

    fun append(value: KuaBoolean) = append(value.value)
    fun append(value: Boolean): Int {
        state.native.pushBoolean(value)
        return state.tableAppend(index)
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

    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: KuaNumber) = append(value.value)
    fun append(value: Double): Int {
        state.native.pushNumber(value)
        return state.tableAppend(index)
    }

    fun append(value: KuaDecimal): Int {
        state.native.pushDecimal(value)
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

    fun append(value: KuaString) = append(value.value)
    fun append(value: String): Int {
        state.native.pushString(value)
        return state.tableAppend(index)
    }

    fun append(value: TableProxyMap): Int {
        state.pushTable(value)
        return state.tableAppend(index)
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