package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import java.lang.reflect.Type
import kotlin.reflect.KClass

sealed interface KuaTable : KuaTableType {

    val size: Int
    val isArray: Boolean
    val isMap: Boolean

    interface Array : KuaTable {
        //FIXME this is a temp hack
        val underlyingArray: kotlin.collections.Map<Int, KuaType>
//        val entries: Set<kotlin.collections.Map.Entry<Int, KuaType>> get() = underlyingArray.entries
//        val keys: Set<Int> get() = underlyingArray.keys
//        val values: Collection<KuaType> get() = underlyingArray.values

        fun asSequence(): Sequence<KuaType>

        fun append(value: KuaTable): Int
        fun append(value: KuaType): Int
        fun append(value: KuaAny): Int
        operator fun get(key: Int): KuaType
        fun getBoolean(idx: Int): Boolean
        fun getBooleanValue(idx: Int): KuaBoolean

        fun append(value: Boolean): Int
        fun append(value: KuaBoolean): Int

        fun getInt(idx: Int) = getNumberType(idx).value.toInt()
        fun getLong(idx: Int) = getNumberType(idx).value.toLong()
        fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
        fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
        fun getNumberType(idx: Int): KuaNumber
        fun getDecimalType(idx: Int): KuaDecimal

        fun append(value: Int) = append(value.toDouble())
        fun append(value: Long) = append(value.toDouble())
        fun append(value: Float) = append(value.toDouble())
        fun append(value: Double) = append(KuaNumber(value))
        fun append(value: KuaNumber): Int
        fun append(value: KuaDecimal): Int

        fun append(value: SnowflakeId) = append(value.value.toString(16))
        fun append(value: ValueObjectId) = append(value.value.value)

        fun getString(idx: Int) = getStringType(idx).value
        fun getStringType(idx: Int): KuaString

        fun append(value: String) = append(KuaString(value))
        fun append(value: KuaString): Int

        fun getCodeType(idx: Int) = KuaCode(getString(idx))

        fun type(idx: Int): KClass<out KuaType>
    }

    interface Map : KuaTable {

        fun entries(): Sequence<Pair<KuaString, KuaType>>

//        val entries: Set<kotlin.collections.Map.Entry<String, KuaType>> get() = underlyingMap.entries
//        val keys: Set<String> get() = underlyingMap.keys
//        val values: Collection<KuaType> get() = underlyingMap.values

        fun getArray(key: String): Array
        fun findArray(key: String): Array?

        operator fun get(key: String): KuaType
        operator fun set(key: String, value: KuaType): Int
        operator fun set(key: String, value: KuaAny) = set(key, value.value)

        fun unset(key: KuaString) = unset(key.value)

        @Suppress("UNUSED_PARAMETER")
        operator fun set(key: String, value: KuaNil) = unset(key)

        @Suppress("UNUSED_PARAMETER")
        operator fun set(key: KuaString, value: KuaNil) = unset(key.value)
        fun unset(key: String): Int
        fun getBooleanValue(key: KuaString) = getBooleanValue(key.value)
        fun getBoolean(key: String): Boolean = getBooleanValue(key).value
        fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
        fun getBooleanValue(key: String): KuaBoolean

        operator fun set(key: KuaString, value: KuaBoolean) = set(key, value.value)
        operator fun set(key: KuaString, value: Boolean) = set(key.value, booleanOf(value))
        operator fun set(key: String, value: Boolean) = set(key, booleanOf(value))
        operator fun set(key: String, value: KuaBoolean): Int

        fun getCodeType(key: String) = KuaCode(getString(key))
        fun getCodeType(key: KuaString) = getCodeType(key.value)
        operator fun set(key: KuaString, value: KuaCode) = set(key.value, value)
        operator fun set(key: String, value: KuaCode): Int

        operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
        operator fun set(key: KuaString, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
        operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
        operator fun set(key: KuaString, value: SnowflakeId) = set(key.value, value.value.toString(16))

        fun getInt(key: String): Int = getNumberValue(key).value.toInt()
        fun getInt(key: KuaString) = getInt(key.value)
        fun getLong(key: String): Long = getNumberValue(key).value.toLong()
        fun getLong(key: KuaString): Long = getLong(key.value)
        fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
        fun getFloat(key: KuaString): Float = getFloat(key.value)
        fun getDouble(key: String): Double = getNumberValue(key).value
        fun getDouble(key: KuaString): Double = getDouble(key.value)
        fun getNumberValue(key: KuaString): KuaNumber = getNumberValue(key.value)
        fun getNumberValue(key: String): KuaNumber

        operator fun set(key: String, value: Int) = set(key, value.toDouble())
        operator fun set(key: String, value: Long) = set(key, value.toDouble())
        operator fun set(key: String, value: Float) = set(key, value.toDouble())
        operator fun set(key: String, value: Double) = set(key, KuaNumber(value))
        operator fun set(key: KuaString, value: KuaNumber) = set(key.value, value.value)
        operator fun set(key: String, value: KuaNumber): Int

        operator fun set(key: String, value: KuaTable): Int

        fun getStringType(key: KuaString) = getStringType(key.value)
        fun getString(key: String): String = getStringType(key).value
        fun getString(key: KuaString): String = getString(key.value)
        fun getStringType(key: String): KuaString

        fun findStringType(key: KuaString) = findStringType(key.value)
        fun findString(key: String): String? = findStringType(key)?.value
        fun findString(key: KuaString): String? = findString(key.value)
        fun findStringType(key: String): KuaString?

        operator fun set(key: String, value: String) = set(key, KuaString(value))
        operator fun set(key: KuaString, value: KuaString) = set(key.value, value.value)
        operator fun set(key: String, value: KuaString): Int

        fun type(key: String): KClass<out KuaType>
    }

    companion object {
        fun Array(vararg pairs: Pair<Int, KuaType>): KuaTableDefaultImpl =
            KuaTableDefaultImpl(mutableMapOf(), mutableMapOf(*pairs))

        fun Array(array: kotlin.collections.Map<Int, KuaType>): KuaTableDefaultImpl =
            KuaTableDefaultImpl(mutableMapOf(), array.toMutableMap())

        fun Map(vararg pairs: Pair<String, KuaType>): KuaTableDefaultImpl =
            KuaTableDefaultImpl(mutableMapOf(*pairs), mutableMapOf())

        fun Map(map: kotlin.collections.Map<String, KuaType>): KuaTableDefaultImpl =
            KuaTableDefaultImpl(map.toMutableMap(), mutableMapOf())
    }


    object Adapter : JsonAdapter<KuaTable> {
        override fun serialize(instance: KuaTable, type: Type, ctx: JsonSerializationContext): JsonElement {
            TODO()
//            val valueBuilder = HotObject.builder()
//            instance.forEach { (key, value) ->
//                valueBuilder.set(key, GsonTransform.toNode(ctx.serialize(value)))
//            }
//            return ctx.serialize(
//                HotObject.builder()
//                    .set("value", valueBuilder.build())
//                    .set("type", instance.type.name)
//                    .build()
//            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): KuaTable {
//            val obj = element.asJsonObject.get("value").asJsonObject
//            val map = mutableMapOf<String, KuaType>()
//            obj.keySet().forEach { key ->
//                map[key] = ctx.deserialize(obj.get(key), KuaType::class.java)
//            }
//            return KuaMap(map)
            TODO()
        }
    }
}

data class KuaTableDefaultImpl(
    val underlyingMap: MutableMap<String, KuaType> = mutableMapOf(),
    override val underlyingArray: MutableMap<Int, KuaType> = mutableMapOf()
) : KuaTable.Map, KuaTable.Array {

    override val type: KuaType.Type = KuaType.Type.Table

    override val size: Int = if (underlyingMap.isNotEmpty()) {
        underlyingMap.size
    } else {
        underlyingArray.size
    }

    // FIXME is this required?
    override val isMap: Boolean get() = underlyingMap.isNotEmpty() || underlyingArray.isEmpty()

    // FIXME is this required?
    override val isArray: Boolean get() = !isMap

    override fun asSequence(): Sequence<KuaType> {
        return underlyingArray.values.asSequence()
    }

    override fun entries(): Sequence<Pair<KuaString, KuaType>> {
        return underlyingMap.asSequence().map { (key, value) -> KuaString(key) to value }
    }


    override fun append(value: KuaTable): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }

    override fun append(value: KuaType): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }

    override fun append(value: KuaAny) = append(value.value)

    override operator fun get(key: Int): KuaType {
        return underlyingArray[key]!!
    }

    override fun getBoolean(idx: Int) = getBooleanValue(idx) == KuaTrue
    override fun getBooleanValue(idx: Int): KuaBoolean {
        checkExpectedType(idx, KuaBoolean::class)
        return underlyingArray[idx]!! as KuaBoolean
    }

    override fun append(value: Boolean) = append(booleanOf(value))
    override fun append(value: KuaBoolean): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }


    override fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    override fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    override fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    override fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    override fun getNumberType(idx: Int): KuaNumber {
        checkExpectedType(idx, KuaNumber::class)
        return underlyingArray[idx]!! as KuaNumber
    }

    override fun getDecimalType(idx: Int): KuaDecimal {
        checkExpectedType(idx, KuaDecimal::class)
        return underlyingArray[idx]!! as KuaDecimal
    }


    override fun append(value: Int) = append(value.toDouble())
    override fun append(value: Long) = append(value.toDouble())
    override fun append(value: Float) = append(value.toDouble())
    override fun append(value: Double) = append(KuaNumber(value))
    override fun append(value: KuaNumber): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }

    override fun append(value: KuaDecimal): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }


    override fun append(value: SnowflakeId) = append(value.value.toString(16))
    override fun append(value: ValueObjectId) = append(value.value.value)

    override fun getString(idx: Int) = getStringType(idx).value
    override fun getStringType(idx: Int): KuaString {
        checkExpectedType(idx, KuaString::class)
        return underlyingArray[idx]!! as KuaString
    }

    override fun append(value: String) = append(KuaString(value))
    override fun append(value: KuaString): Int {
        this.underlyingArray[this.underlyingArray.size + 1] = value
        return underlyingArray.size
    }

    override fun getCodeType(idx: Int) = KuaCode(getString(idx))

    override fun type(idx: Int): KClass<out KuaType> {
        return underlyingArray[idx]?.let { it::class } ?: KuaNil::class
    }


    override fun getArray(key: String): KuaTable.Array =
        findArray(key) ?: throw NoSuchElementException("$key not found")

    override fun findArray(key: String): KuaTable.Array? {
        checkExpectedType(key, KuaTable.Array::class)
        return underlyingMap[key]?.let { value -> value as KuaTable.Array }
    }


    override operator fun get(key: String): KuaType = underlyingMap[key] ?: KuaNil
    override operator fun set(key: String, value: KuaType): Int {
        this.underlyingMap[key] = value
        return underlyingMap.size
    }

    override operator fun set(key: String, value: KuaAny) = set(key, value.value)

    override fun unset(key: KuaString) = unset(key.value)

    @Suppress("UNUSED_PARAMETER")
    override operator fun set(key: String, value: KuaNil) = unset(key)

    @Suppress("UNUSED_PARAMETER")
    override operator fun set(key: KuaString, value: KuaNil) = unset(key.value)
    override fun unset(key: String): Int {
        underlyingMap.remove(key)
        return underlyingMap.size
    }

    override fun getBooleanValue(key: KuaString) = getBooleanValue(key.value)
    override fun getBoolean(key: String): Boolean = getBooleanValue(key).value
    override fun getBoolean(key: KuaString): Boolean = getBoolean(key.value)
    override fun getBooleanValue(key: String): KuaBoolean {
        val type = type(key)
        check(type == KuaTrue::class || type == KuaFalse::class) {
            "Expected type to be boolean but was $type"
        }
        return underlyingMap[key]!! as KuaBoolean
    }

    override operator fun set(key: KuaString, value: KuaBoolean) = set(key, value.value)
    override operator fun set(key: KuaString, value: Boolean) = set(key.value, booleanOf(value))
    override operator fun set(key: String, value: Boolean) = set(key, booleanOf(value))
    override operator fun set(key: String, value: KuaBoolean): Int {
        this.underlyingMap[key] = value
        return underlyingMap.size
    }

    override fun getCodeType(key: String) = KuaCode(getString(key))
    override fun getCodeType(key: KuaString) = getCodeType(key.value)
    override operator fun set(key: KuaString, value: KuaCode) = set(key.value, value)
    override operator fun set(key: String, value: KuaCode): Int {
        this.underlyingMap[key] = KuaString(value.value)
        return underlyingMap.size
    }

    override operator fun set(key: String, value: ValueObjectId) = set(key, value.value.value.toString(16))
    override operator fun set(key: KuaString, value: ValueObjectId) = set(key.value, value.value.value.toString(16))
    override operator fun set(key: String, value: SnowflakeId) = set(key, value.value.toString(16))
    override operator fun set(key: KuaString, value: SnowflakeId) = set(key.value, value.value.toString(16))

    override fun getInt(key: String): Int = getNumberValue(key).value.toInt()
    override fun getInt(key: KuaString) = getInt(key.value)
    override fun getLong(key: String): Long = getNumberValue(key).value.toLong()
    override fun getLong(key: KuaString): Long = getLong(key.value)
    override fun getFloat(key: String): Float = getNumberValue(key).value.toFloat()
    override fun getFloat(key: KuaString): Float = getFloat(key.value)
    override fun getDouble(key: String): Double = getNumberValue(key).value
    override fun getDouble(key: KuaString): Double = getDouble(key.value)
    override fun getNumberValue(key: KuaString): KuaNumber = getNumberValue(key.value)
    override fun getNumberValue(key: String): KuaNumber {
        checkExpectedType(key, KuaNumber::class)
        return underlyingMap[key]!! as KuaNumber
    }

    override operator fun set(key: String, value: Int) = set(key, value.toDouble())
    override operator fun set(key: String, value: Long) = set(key, value.toDouble())
    override operator fun set(key: String, value: Float) = set(key, value.toDouble())
    override operator fun set(key: String, value: Double) = set(key, KuaNumber(value))
    override operator fun set(key: KuaString, value: KuaNumber) = set(key.value, value.value)
    override operator fun set(key: String, value: KuaNumber): Int {
        this.underlyingMap[key] = value
        return underlyingMap.size
    }

    override operator fun set(key: String, value: KuaTable): Int {
        this.underlyingMap[key] = value
        return underlyingMap.size
    }

    override fun getStringType(key: KuaString) = getStringType(key.value)
    override fun getString(key: String): String = getStringType(key).value
    override fun getString(key: KuaString): String = getString(key.value)
    override fun getStringType(key: String): KuaString {
        checkExpectedType(key, KuaString::class)
        return underlyingMap[key]!! as KuaString
    }


    override fun findStringType(key: KuaString) = findStringType(key.value)
    override fun findString(key: String): String? = findStringType(key)?.value
    override fun findString(key: KuaString): String? = findString(key.value)
    override fun findStringType(key: String): KuaString? {
        if (isNull(key)) {
            return null
        }
        checkExpectedType(key, KuaString::class)
        return underlyingMap[key]!! as KuaString
    }


    override operator fun set(key: String, value: String) = set(key, KuaString(value))
    override operator fun set(key: KuaString, value: KuaString) = set(key.value, value.value)
    override operator fun set(key: String, value: KuaString): Int {
        this.underlyingMap[key] = value
        return underlyingMap.size
    }

    override fun type(key: String): KClass<out KuaType> {
        return underlyingMap[key]?.let { it::class } ?: KuaNil::class
    }


    // is array if only array contains data
    // is map otherwise
}

internal fun KuaTable.Array.isNull(idx: Int) = type(idx) == KuaNil::class

internal fun KuaTable.Array.checkExpectedType(idx: Int, expected: KClass<out KuaType>) {
    check(type(idx) == expected) {
        "Expected type to be $expected but was $this"
    }
}

internal fun KuaTable.Map.isNull(key: String) = type(key) == KuaNil::class

internal fun KuaTable.Map.checkExpectedType(key: String, expected: KClass<out KuaType>) {
    check(type(key) == expected) {
        "Expected type to be $expected but was $this"
    }
}